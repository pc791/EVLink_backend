package com.evlink.domain.login.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.evlink.domain.login.dao.AuthDao;
import com.evlink.domain.login.service.PasswordLessLoginService;
import com.evlink.domain.login.vo.PasswordLessUserInfoVO;
import com.evlink.domain.login.vo.UserVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/PLogin/")
public class PasswordLessLoginController {
	@Autowired
	private PasswordLessLoginService plLoginService;
	@Autowired
	private AuthDao authDao;
	private final SecurityContextRepository securityContextRepository;
	
	@Value("${passwordless.corpId}")
	private String corpId;
	
	@Value("${passwordless.serverId}")
	private String serverId;
	
	@Value("${passwordless.serverKey}")
	private String serverKey;
	
	@Value("${passwordless.simpleAutopasswordUrl}")
	private String simpleAutopasswordUrl;
	
	@Value("${passwordless.restCheckUrl}")
	private String restCheckUrl;
	
	@Value("${passwordless.pushConnectorUrl}")
	private String pushConnectorUrl;
	
	@Value("${passwordless.recommend}")
	private String recommend;
	
	// Passwordless URL
	private String isApUrl = "/ap/rest/auth/isAp";									// Check Passwordless Registration Status
	private String joinApUrl = "/ap/rest/auth/joinAp";								// Passwordless Registration REST API
	private String withdrawalApUrl = "/ap/rest/auth/withdrawalAp";					// Passwordless Deregistration REST API
	private String getTokenForOneTimeUrl = "/ap/rest/auth/getTokenForOneTime";		// Passwordless One-Time Token Request REST API
	private String getSpUrl = "/ap/rest/auth/getSp";								// Passwordless Authentication Request REST API
	private String resultUrl = "/ap/rest/auth/result";								// Passwordless Authentication Result Request REST API
	private String cancelUrl = "/ap/rest/auth/cancel";								// Passwordless Authentication Request Cancellation REST API
	
	// Login
	@PostMapping(value="loginCheck", produces="application/json;charset=utf8")
	public Map<String, Object> loginCheck(
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "pw", required = false) String pw,
			HttpServletRequest request) {
	
		if(id == null)	id = "";
		if(pw == null)	pw = "";

		log.info("loginCheck : [" + id + "] / ["  + pw + "]");

		Map<String, Object> mapResult = new HashMap<String, Object>();

		if(!id.equals("") && !pw.equals("")) {
			
			PasswordLessUserInfoVO userinfo = new PasswordLessUserInfoVO();
			userinfo.setLogin_id(id);
			userinfo.setLogin_pw(pw);
			PasswordLessUserInfoVO newUserinfo = plLoginService.checkPassword(userinfo);
			
			boolean exist = false;
			// 2025.09.15
			HttpSession session = request.getSession(true);
			
			if(newUserinfo != null) {
				ModelMap modelMap = passwordlessCallApi(session, request, null, "isApUrl", "userId=" + id);
				if(modelMap != null) {
					String result = (String) modelMap.getAttribute("result");
					if(result.equals("OK")) {
						String data = (String) modelMap.getAttribute("data");
						if(data != null && !data.equals("")) {
							JSONParser parser = new JSONParser();
							try {
								JSONObject jsonResponse = (JSONObject)parser.parse(data);
							    JSONObject jsonData = (JSONObject) jsonResponse.get("data");
							    System.out.println("data=" + jsonData.toString());
							    exist = (boolean) (jsonData).get("exist");
							} catch(ParseException pe) {
								pe.printStackTrace();
							}
						}
					}
				}

				log.info("recommend=" + recommend + ", exist=" + exist);
				
				if(recommend.equals("1") && exist) {
					mapResult.put("result", "패스워드로 로그인하고 싶다면\\nPasswordless 서비스를 먼저 해지하세요.");
				}
				else {
					//HttpSession session = request.getSession(true); 2025.09.15
					session.setAttribute("id", id);
					
					mapResult.put("result", "OK");
				}
			}
			else {
				mapResult.put("result", "아이디 또는 패스워드가 올바르지 않습니다");
			}
		}
		
		return mapResult;
	}
	
	// Sign
	@PostMapping(value="join", produces="application/json;charset=utf8")
	public Map<String, Object> join(
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "pw", required = false) String pw,
			HttpServletRequest request) {
	
		if(id == null)		id = "";
		if(pw == null)		pw = "";

		Map<String, Object> mapResult = new HashMap<String, Object>();

		if(!id.equals("") && !pw.equals("")) {
			
			PasswordLessUserInfoVO userinfo = new PasswordLessUserInfoVO();
			userinfo.setLogin_id(id);
			PasswordLessUserInfoVO newUserinfo = plLoginService.getUserInfo(userinfo);
			
			if(newUserinfo != null) {
				log.info("join failed");
				String tmp_result = "ID [@@@] 이미 존재합니다.";
				mapResult.put("result", tmp_result.replace("@@@", id));
			}
			else {
				userinfo.setLogin_pw(pw);
				plLoginService.createUserInfo(userinfo);
				log.info("join completed.");

				mapResult.put("result", "OK");
			}
		}
		
		return mapResult;
	}
	
	// Account Deletion
	@PostMapping(value="withdraw", produces="application/json;charset=utf8")
	public Map<String, Object> withdraw(HttpServletRequest request) {
	
		HttpSession session = request.getSession(true);
		String id = (String) session.getAttribute("id");

		if(id == null)		id = "";

		log.info("withdraw : [" + id + "]");

		Map<String, Object> mapResult = new HashMap<String, Object>();

		if(!id.equals("")) {
			
			PasswordLessUserInfoVO userinfo = new PasswordLessUserInfoVO();
			userinfo.setLogin_id(id);
			plLoginService.withdrawUserInfo(userinfo);
			
			session.setAttribute("id", null);
			log.info("withdraw : [" + id + "] completed.");
		}
		
		mapResult.put("result", "OK");

		return mapResult;
	}
	
	// Change Password
	@PostMapping(value="changepw", produces="application/json;charset=utf8")
	public Map<String, Object> changepw(
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "pw", required = false) String pw,
			HttpServletRequest request) {
	
		if(id == null)		id = "";
		if(pw == null)		pw = "";

		Map<String, Object> mapResult = new HashMap<String, Object>();

		if(!id.equals("") && !pw.equals("")) {
			
			PasswordLessUserInfoVO userinfo = new PasswordLessUserInfoVO();
			userinfo.setLogin_id(id);
			PasswordLessUserInfoVO newUserinfo = plLoginService.getUserInfo(userinfo);
			
			if(newUserinfo == null) {
				log.info("changepw failed");
				String tmp_result = "ID [@@@] 존재하지 않습니다.";
				mapResult.put("result", tmp_result.replace("@@@", id));
			}
			else {
				userinfo.setLogin_pw(pw);
				plLoginService.changepw(userinfo);
				log.info("changepw completed.");

				mapResult.put("result", "OK");
			}
		}
		else {
			mapResult.put("result", "아이디 또는 패스워드가 올바르지 않습니다.");
		}

		return mapResult;
	}
	
	// Logout
	@PostMapping(value="logout", produces="application/json;charset=utf8")
	public Map<String, Object> logout(HttpServletRequest request) {
	
		Map<String, Object> mapResult = new HashMap<String, Object>();
		HttpSession session = request.getSession(true);
		String id = (String) session.getAttribute("id");
		log.info("logout : [" + id + "]");
		
		session.setAttribute("id", null);
		mapResult.put("result", "OK");
		
		log.info("logout : [" + id + "] completed.");
		
		return mapResult;
	}
	
	// ------------------------------------------------ Passwordless ------------------------------------------------
	
	// Login
	@PostMapping(value="passwordlessManageCheck", produces="application/json;charset=utf8")
	public Map<String, Object> passwordlessManageCheck(
		@RequestParam(value = "id", required = false) String id,
		@RequestParam(value = "pw", required = false) String pw,
		HttpServletRequest request) {
	
		if(id == null)	id = "";
		if(pw == null)	pw = "";

		log.info("passwordlessManageCheck : id [" + id + "] pw ["  + pw + "]");

		Map<String, Object> mapResult = new HashMap<String, Object>();

		if(!id.equals("") && !pw.equals("")) {
			
			PasswordLessUserInfoVO userinfo = new PasswordLessUserInfoVO();
			userinfo.setLogin_id(id);
			userinfo.setLogin_pw(pw);
			PasswordLessUserInfoVO newUserinfo = plLoginService.checkPassword(userinfo);
			
			if(newUserinfo != null) {
				String tmpToken = java.util.UUID.randomUUID().toString();
				String tmpTime = Long.toString(System.currentTimeMillis());
				
				log.info("passwordlessManageCheck : token [" + tmpToken + "] time [" + tmpTime + "]");
				
				HttpSession session = request.getSession(true);
				session.setAttribute("PasswordlessToken", tmpToken);
				session.setAttribute("PasswordlessTime", tmpTime);
				
				mapResult.put("PasswordlessToken", tmpToken);
				mapResult.put("result", "OK");
			}
			else {
				mapResult.put("result", "아이디 또는 패스워드가 올바르지 않습니다.");
			}
		}
		else {
			mapResult.put("result", "ID 또는 Password 값이 없습니다.");
		}
		
		return mapResult;
	}
	
	@RequestMapping(value="/passwordlessCallApi")
	public ModelMap passwordlessCallApi(
			HttpSession session,
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "url", required = false) String url,
			@RequestParam(value = "params", required = false) String params
			){

		ModelMap modelMap = new ModelMap();
		String result = "";

		boolean existMember = false;
		
		if(url == null)		url = "";
		if(params == null)	params = "";
		
		Map<String, String> mapParams = getParamsKeyValue(params);
		
		String userId = "";
		String userToken = "";
		
		// 2025.09.15
		// HttpSession session = request.getSession();
		session = request.getSession();
		String sessionUserToken = (String) session.getAttribute("PasswordlessToken");
		String sessionTime = (String) session.getAttribute("PasswordlessTime");
		
		if(sessionUserToken == null)	sessionUserToken = "";
		if(sessionTime == null)			sessionTime = "";
		
		long nowTime = System.currentTimeMillis();
		long tokenTime = 0L;
		int gapTime = 0;
		try {
			tokenTime = Long.parseLong(sessionTime);
			gapTime = (int) (nowTime - tokenTime);
		}
		catch(Exception e) {
			gapTime = 99999999;
		}
		
		userId = mapParams.get("userId");
		userToken = mapParams.get("token");
		
		boolean matchToken = false;
		if(!sessionUserToken.equals("") && sessionUserToken.equals(userToken))
			matchToken = true;
		
		log.info("passwordlessCallApi : [" + url + "] userId=" + userId + ", Token Match [" + matchToken + "] userToken[" + userToken + "], sessionUserToken [" + sessionUserToken + "], gapTime [" + gapTime + "]");
		
		if(userId == null)		userId = "";
		if(userToken == null)	userToken = "";
		
		// Identity verification for QR request and cancellation
		if (url.equals("joinApUrl") || url.equals("withdrawalApUrl")) {
		    // If the user is not logged in for Passwordless settings
		    if (!matchToken) {
		        modelMap.put("result", "비정상적인 유저입니다.");
		        return modelMap;
		    }
		    // If more than 5 minutes have passed after logging in for Passwordless settings, handle as Timeout
		    else if (gapTime > 5 * 60 * 1000) {
		        modelMap.put("result", "Passwordless 서비스 토큰이 만료되었습니다.");
		        return modelMap;
		    }
		}

		
		if(!url.equals("resultUrl")) {
			log.info("passwordlessCallApi : url [" + url + "] params [" + params + "] userId [" + userId + "]");
		}
		
		PasswordLessUserInfoVO userinfo = new PasswordLessUserInfoVO();
		userinfo.setLogin_id(userId);
		PasswordLessUserInfoVO newUserinfo = plLoginService.getUserInfo(userinfo);
		
		if(newUserinfo == null) {
			String tmp_result = "ID [@@@] 존재하지 않습니다.";	// ID [" + id + "] does not exist.
			modelMap.put("result", tmp_result.replace("@@@", userId));
			return modelMap;
		}
		else {
			String random = java.util.UUID.randomUUID().toString();
	    	String sessionId = System.currentTimeMillis() + "_sessionId";
	    	String apiUrl = "";
	    	String ip = request.getRemoteAddr();
	    	
	    	if(ip.equals("0:0:0:0:0:0:0:1"))
	    		ip = "127.0.0.1";
	    	
	    	if(url.equals("isApUrl"))				{ apiUrl = isApUrl; }
			if(url.equals("joinApUrl"))				{ apiUrl = joinApUrl; }
			if(url.equals("withdrawalApUrl"))		{ apiUrl = withdrawalApUrl; }
			if(url.equals("getTokenForOneTimeUrl"))	{ apiUrl = getTokenForOneTimeUrl; }
			if(url.equals("getSpUrl"))				{ apiUrl = getSpUrl; params += "&clientIp=" + ip + "&sessionId=" + sessionId + "&random=" + random + "&password="; }
			if(url.equals("resultUrl"))				{ apiUrl = resultUrl;}
			if(url.equals("cancelUrl"))				{ apiUrl = cancelUrl;}
			
			if(!url.equals("resultUrl")) {
				log.info("passwordlessCallApi : url [" + url + "], param [" + params + "], apiUrl [" + apiUrl + "]");
			}
			
			if(!apiUrl.equals("")) {
				try {
					if(!url.equals("getSpUrl") && !url.equals("resultUrl")) {
						log.info("passwordlessCallApi : url [" + (restCheckUrl + apiUrl + "?" + params) + "]");
					}

					result = callApi("POST", restCheckUrl + apiUrl, params);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			if(!url.equals("getSpUrl") && !url.equals("resultUrl")) {
				log.info("passwordlessCallApi : result [" + result + "]");
			}

			// One-Time Token Request
			if(url.equals("getTokenForOneTimeUrl")) {
				String token = "";
				String oneTimeToken = "";
				
				JSONParser parser = new JSONParser();
				try {
					JSONObject jsonResponse = (JSONObject)parser.parse(result);
				    JSONObject jsonData = (JSONObject) jsonResponse.get("data");
				    token = (String) (jsonData).get("token");
				    oneTimeToken = getDecryptAES(token, serverKey.getBytes());
				} catch(ParseException pe) {
					pe.printStackTrace();
				}
				log.info("passwordlessCallApi : token [" + token + "] --> oneTimeToken [" + oneTimeToken + "]");
				
				modelMap.put("oneTimeToken", oneTimeToken);
			}
			
			// Passwordless Authentication Request REST API
			if(url.equals("getSpUrl")) {
				modelMap.put("sessionId", sessionId);
			}
			
			// Passwordless QR Registration Waiting WebSocket URL
			if(url.equals("joinApUrl")) {
				modelMap.put("pushConnectorUrl", pushConnectorUrl);
			}
			
			// Passwordless QR Registration Waiting WebSocket URL
			if(url.equals("isApUrl")) {
				log.info("passwordlessCallApi : mapParams=" + mapParams.toString());
				try {
					String isQRReg = mapParams.get("QRReg");
					if(isQRReg.equals("T")) {
						JSONParser parser = new JSONParser();
						JSONObject jsonResponse = (JSONObject)parser.parse(result);
					    JSONObject jsonData = (JSONObject) jsonResponse.get("data");
					    boolean exist = (boolean) (jsonData).get("exist");
					    
					    if(exist) {
					    	  // Change password after QR registration is complete
					    	log.info("passwordlessCallApi : QR Registration Complete --> Change Password");
					    	String newPw = Long.toString(System.currentTimeMillis()) + ":" + userId;
							userinfo.setLogin_id(userId);
							userinfo.setLogin_pw(newPw);
							plLoginService.changepw(userinfo);
							userinfo.setUser_tp("USR001");
							plLoginService.updateUserType(userinfo);
					    }
					}
				}
				catch(NullPointerException npe) {
					//
				} catch(ParseException pe) {
					//
				}
			}
			
			// Passwordless Approval Waiting
			if(url.equals("resultUrl")) {
				JSONParser parser = new JSONParser();
				try {
					JSONObject jsonResponse = (JSONObject)parser.parse(result);
				    JSONObject jsonData = (JSONObject) jsonResponse.get("data");
				    
				    if(jsonData != null) {
					    String auth = (String) (jsonData).get("auth");
		
					    if(auth != null && auth.equals("Y")) {
					    	// Change password upon successful login
					    	log.info("passwordlessCallApi : Login Success --> Change Password");
							String newPw = Long.toString(System.currentTimeMillis()) + ":" + userId;
							userinfo = new PasswordLessUserInfoVO();
							userinfo.setLogin_id(userId);
							userinfo.setLogin_pw(newPw);
							plLoginService.changepw(userinfo);
							// 2025.09.16
							Map<String, String> usrMap = new HashMap<>();
							usrMap.put("login_id", newUserinfo.getLogin_id());
					        usrMap.put("join_type", newUserinfo.getJoin_type());
					        int userCnt = authDao.checkSignUp(usrMap);
					        // System.out.println("userCnt >> "+userCnt);
					        if(userCnt > 0) {
					        	UserVO usrvo = authDao.getUserInfo(usrMap);
					        	// System.out.println("usrvo >> "+usrvo.toString());
					        	// login session 값 설정
					        	Map<String, Object> std = new HashMap<>();
					        	std.put("email", usrvo.getLogin_id());
					            std.put("provider", "passwordless");
					            std.put("userId", usrvo.getUser_id());
					        	std.put("userTp", usrvo.getUser_tp());
					        	
					        	List<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority("ROLE_USER"));
					        	OAuth2User oAuth2User = new DefaultOAuth2User(roles, std, "email");
					        	
					        	String registrationId = "passwordless";
					        	Authentication authToken = new OAuth2AuthenticationToken(oAuth2User, roles, registrationId);
					        	((AbstractAuthenticationToken) authToken).setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					        	
					        	SecurityContext context = SecurityContextHolder.createEmptyContext();
					        	context.setAuthentication(authToken);
					        	SecurityContextHolder.setContext(context);
					        	securityContextRepository.saveContext(context, request, response);
					        }
					        // end
						}
				    }
				} catch(ParseException pe) {
					pe.printStackTrace();
				}
			}
			
			// 패스워드리스 해지시
			if(url.equals("withdrawalApUrl")) {
				JSONParser parser = new JSONParser();
				try {
					JSONObject jsonResponse = (JSONObject)parser.parse(result);
					boolean resultTag = (boolean) (jsonResponse).get("result");
				    				    
				    if(resultTag) {
				    	userinfo.setUser_tp("USR005");
						plLoginService.updateUserType(userinfo);
				    }
				    
				} catch(ParseException pe) {
					pe.printStackTrace();
				}
			}

			modelMap.put("result", "OK");
		}
		
		modelMap.put("data", result);
		
		return modelMap;
	}
	
	public String callApi(String type, String requestURL, String params) {

 		String retVal = "";
 		Map<String, String> mapParams = getParamsKeyValue(params);

 		try {
			URIBuilder b = new URIBuilder(requestURL);
			
			Set<String> set = mapParams.keySet();
			Iterator<String> keyset = set.iterator();
			while(keyset.hasNext()) {
				String key = keyset.next();
				String value = mapParams.get(key);
				b.addParameter(key, value);
			}
			URI uri = b.build();
		
		    CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		    
		    org.apache.http.HttpResponse response;
		    
		    if(type.toUpperCase().equals("POST")) {
		        HttpPost httpPost = new HttpPost(uri);
		        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
		    	response = httpClient.execute(httpPost);
		    }
		    else {
		    	HttpGet httpGet = new HttpGet(uri);
		    	httpGet.addHeader("Content-Type", "application/x-www-form-urlencoded");
		    	response = httpClient.execute(httpGet);
		    }
		    
		    HttpEntity entity = response.getEntity();
		    retVal = EntityUtils.toString(entity);
		} catch(Exception e) {
			System.out.println(e.toString());
		}
		
		return retVal;
 	}
	
	public Map<String, String> getParamsKeyValue(String params) {
 		String[] arrParams = params.split("&");
         Map<String, String> map = new HashMap<String, String>();
         for (String param : arrParams)
         {
         	String name = "";
         	String value = "";
         	
         	String[] tmpArr = param.split("=");
             name = tmpArr[0];
             
             if(tmpArr.length == 2)
             	value = tmpArr[1];
             
             map.put(name, value);
         }

         return map;
 	}
	
	private static String getDecryptAES(String encrypted, byte[] key) {
 		String strRet = null;
 		
 		byte[]  strIV = key;
 		if ( key == null || strIV == null ) return null;
 		try {
 			SecretKey secureKey = new SecretKeySpec(key, "AES");
 			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
 			c.init(Cipher.DECRYPT_MODE, secureKey, new IvParameterSpec(strIV));
 			byte[] byteStr = java.util.Base64.getDecoder().decode(encrypted);//Base64Util.getDecData(encrypted);
 			strRet = new String(c.doFinal(byteStr), "utf-8");
 		} catch (Exception e) {
 			e.printStackTrace();
 		}
 		return strRet;	
 	}
	
}
