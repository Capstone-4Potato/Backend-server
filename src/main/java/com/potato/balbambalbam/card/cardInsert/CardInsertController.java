package com.potato.balbambalbam.card.cardInsert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
@Slf4j
@RequiredArgsConstructor
public class CardInsertController {
    private final CardInsertService cardInsertService;

    //TODO : 개발자 권한을 가진 access토큰만 해당 url 요청을 할 수 있도록 해야함
    @GetMapping("/cards/insert")
    public ResponseEntity<Object> cardUpdateListener(@RequestHeader("access") String access) {
        int cardListCnt = cardInsertService.updateCardRecordList();
        return ResponseEntity.ok("Total" + cardListCnt + "update success");
    }

    @GetMapping("/cards/today/insert")
    public ResponseEntity<Object> todayCardUpdateListener(@RequestHeader("access") String access) {
        int cardListCnt = cardInsertService.updateTodayCardRecordList();
        return ResponseEntity.ok("Total" + cardListCnt + "update success");
    }
}