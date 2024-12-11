package com.potato.balbambalbam.user.attendance.service;

import com.potato.balbambalbam.data.entity.UserAttendance;
import com.potato.balbambalbam.data.repository.UserAttendanceRepository;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final UserAttendanceRepository userAttendanceRepository;

    public Map<String, List<Integer>> getAttendanceDates(Long userId) {
        List<UserAttendance> attendances = userAttendanceRepository.findAllByUserId(userId);

        return attendances.stream()
                .collect(Collectors.groupingBy(
                        // 키를 "YYYY-MM" 형식으로 생성
                        attendance -> attendance.getAttendanceDate()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM")),
                        // 값을 해당 월의 일자(day)만 추출하여 리스트로 생성
                        Collectors.mapping(
                                attendance -> attendance.getAttendanceDate().getDayOfMonth(),
                                Collectors.toList()
                        )
                ));
    }

}
