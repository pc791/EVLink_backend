package com.evlink.domain.reservation.controller;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.evlink.domain.reservation.service.ReservationService;
import com.evlink.domain.reservation.vo.ReservationVO;
import com.evlink.global.vo.PageVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


@RestController
@RequestMapping("/api/reservation")
public class ReservationController {
	@Autowired
	ReservationService reservationService;
	@Autowired
	private PageVO pageVO;
	private int startPage;
	private int endPage;
	
	// 예약
	@PostMapping("")
	public ResponseEntity<Map<String, Object>> reservation(@RequestBody ReservationVO vo) {
		Map<String, Object> response = new HashMap<>();

		System.out.println("Received VO object: " + vo.toString());

		if (vo.getUserId() == null) {
			response.put("success", false);
			response.put("message", "오류: userId가 누락되었습니다.");
			return ResponseEntity.badRequest().body(response);
		}
		if (vo.getChargerId() == null) {
			response.put("success", false);
			response.put("message", "오류: 충전소 ID가 누락되었습니다.");
			return ResponseEntity.badRequest().body(response);
		}

		try {
			LocalTime startTime = vo.getResStartTime();
			LocalTime endTime = vo.getResEndTime();

			if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
				response.put("success", false);
				response.put("message", "오류: 종료 시간이 시작 시간보다 같거나 빠를 수 없습니다.");
				return ResponseEntity.badRequest().body(response);
			}

			// reservationService.reservation() 메서드가 int를 반환하도록 수정
			int result = reservationService.reservation(vo);
			if (result > 0) {
				response.put("success", true);
				response.put("message", "예약 성공!");
				return ResponseEntity.ok(response);
			} else {
				response.put("success", false);
				response.put("message", "예약에 실패했습니다. (알 수 없는 오류)");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
			}

		} catch (IllegalStateException e) {
			response.put("success", false);
			response.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "예약 중 오류가 발생했습니다: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	// 예약 취소
	@PutMapping("/cancel/{resId}")
	public ResponseEntity<Map<String, Object>> reservationCancel(@PathVariable("resId") long resId) {
		Map<String, Object> response = new HashMap<>();

		try {
			int updatedRows = reservationService.reservationCancel(resId);

			if (updatedRows > 0) {
				response.put("success", true);
				response.put("message", "예약 취소 성공!");
				return ResponseEntity.ok(response);
			} else {
				response.put("success", false);
				response.put("message", "취소하려는 예약 정보가 존재하지 않습니다.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}
		} catch (IllegalStateException e) {
			response.put("success", false);
			response.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "예약 취소 중 오류가 발생했습니다.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
	
	// 특정 충전소의 특정 날짜에 대한 예약된 시간 조회
    @GetMapping("/availability")
    public ResponseEntity<Map<String, Object>> getAvailability(
                @RequestParam("chargerId") Long chargerId,
                @RequestParam("date") String date) {
        
        Map<String, Object> response = new HashMap<>();

        try {
            // Service 계층의 getReservedTimes 메서드 호출
            List<Integer> reservedTimes = reservationService.getReservedTimes(chargerId, date);
            
            response.put("success", true);
            response.put("data", reservedTimes); // List<Integer> 반환
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "예약된 시간 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
	
	@GetMapping("/list")
	public Map<String, Object> galleryList(HttpSession session, HttpServletRequest request, @RequestParam Map<String, String> paramMap) {
		// System.out.println("Method =>" + request.getMethod());
		String cPage = paramMap.get("cPage");
		int totalCnt = reservationService.totalCount(paramMap);
		PageVO pageVO = getPageSet(cPage, totalCnt, 10);
		
		Map<String, String> map = new HashMap<>(paramMap);
		map.put("begin", String.valueOf(pageVO.getBeginPerPage()));
		map.put("end", String.valueOf(pageVO.getEndPerPage()));
		List<Map<String, Object>> list = reservationService.reservationList(map);
		
		Map<String, Object> response = new HashMap<>();
		response.put("data", list);                           // 페이징 처리가 완료된 리스트를 저장한 데이터
		response.put("page", pageVO);
		response.put("startPage", getStartPage());   // 시작
		response.put("endPage", getEndPage());       // 끝
		
		return response;
	}
	
	public PageVO getPageSet(String cPage, int totalCnt, int numPerPage) {
		// 보여질 게시물 수 설정
		pageVO.setNumPerPage(numPerPage);
				
		// 총 건수
		pageVO.setTotalRecord(totalCnt);
				
		// 현재페이지
		if(cPage != null) {
			pageVO.setNowPage(Integer.parseInt(cPage));
		}else {
			pageVO.setNowPage(1);
		}
				
		// 총 페이지 수
		int totalPage = (int) Math.ceil(pageVO.getTotalRecord()/(double)pageVO.getNumPerPage());
		pageVO.setTotalPage(totalPage);
		
		// 총 블럭 수
		int totalBlock = (int) Math.ceil(totalPage/(double)pageVO.getPagePerBlock());
		pageVO.setTotalBlock(totalBlock);
				
		// 현재페이지의 시작 게시물과 끝 게시물 번호를 계산 해서 pageVO에 저장
		pageVO.setBeginPerPage((pageVO.getNowPage()-1) * pageVO.getNumPerPage()+1);
		pageVO.setEndPerPage(pageVO.getBeginPerPage() + pageVO.getNumPerPage()-1);
		
		return pageVO;
	}

	// 페이지 블록 시작
	public int getStartPage() {
		startPage = ((pageVO.getNowPage()-1)/pageVO.getPagePerBlock()) * pageVO.getPagePerBlock()+1;
		return startPage;
	}

	// 페이지 블록 끝
	public int getEndPage() {
		endPage = startPage + pageVO.getPagePerBlock()-1;
		
		if(endPage > pageVO.getTotalPage()) {
			endPage = pageVO.getTotalPage();
		}
		return endPage;
	}
}