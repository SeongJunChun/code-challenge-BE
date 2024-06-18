package goorm.code_challenge.room.api;


import goorm.code_challenge.room.domain.ParticipantStatus;
import goorm.code_challenge.global.exception.ApiResponse;
import goorm.code_challenge.global.exception.BaseController;
import goorm.code_challenge.global.exception.CustomException;
import goorm.code_challenge.global.exception.ErrorCode;
import goorm.code_challenge.room.domain.Room;
import goorm.code_challenge.room.dto.request.CreateRoomRequest;
import goorm.code_challenge.room.dto.response.ParticipantInfo;
import goorm.code_challenge.room.dto.response.RoomDTO;
import goorm.code_challenge.room.dto.response.ScoreDTO;
import goorm.code_challenge.room.service.RoomService;
import goorm.code_challenge.room.service.ScoreService;
import goorm.code_challenge.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController extends BaseController {

    private final RoomService roomService;
    private final ScoreService scoreService;

    @PostMapping
    public ResponseEntity<String> createRoom(@Validated @RequestBody CreateRoomRequest roomRequest, Errors errors) {
        if (errors.hasErrors()) {
            for (FieldError error : errors.getFieldErrors()) {
                throw new CustomException(ErrorCode.BAD_REQUEST, error.getDefaultMessage());
            }
        }
        User currentUser = roomService.getCurrentUser();
        Room createdRoom = roomService.createRoom(roomRequest.toEntity(currentUser));
        return new ResponseEntity<>("방이 생성되었습니다.", HttpStatus.CREATED);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDTO> getRoom(@PathVariable("roomId") Long roomId) {
        Room room = roomService.getRoom(roomId);
        RoomDTO roomDTO = new RoomDTO(room);
        return new ResponseEntity<>(roomDTO, HttpStatus.OK);
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<RoomDTO> updateRoom(@PathVariable("roomId") Long roomId, @Validated @RequestBody RoomDTO updatedRoomDTO,Errors errors) {
        if (errors.hasErrors()) {
            for (FieldError error : errors.getFieldErrors()) {
                throw new CustomException(ErrorCode.BAD_REQUEST, error.getDefaultMessage());
            }
        }
        User currentUser = roomService.getCurrentUser();
        Room updatedRoom = roomService.updateRoom(roomId, updatedRoomDTO.toEntity(currentUser));
        return new ResponseEntity<>(new RoomDTO(updatedRoom), HttpStatus.OK);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<String> deleteRoom(@PathVariable("roomId") Long roomId) {
        try {
            roomService.deleteRoom(roomId);
            return new ResponseEntity<>("방이 삭제되었습니다.", HttpStatus.OK);
        } catch (CustomException e) {
            if (e.getErrorCode() == ErrorCode.UNAUTHORIZED) {
                return new ResponseEntity<>(e.GetMessage(), HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(e.GetMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        List<Room> rooms = roomService.getAllRooms();
        List<RoomDTO> roomDTOs = rooms.stream()
                .map(RoomDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(roomDTOs, HttpStatus.OK);
    }

    @PostMapping("/{roomId}/join")
    public ResponseEntity<String> joinRoom(@PathVariable("roomId") Long roomId) {
        try {
            roomService.addUserToRoom(roomId);
            return ResponseEntity.ok("참가 완료");
        } catch (RoomFullException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("방이 가득 찼습니다.");
        } catch (RoomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 방을 찾을 수 없습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("알 수 없는 오류가 발생했습니다.");
        }
    }

    @PostMapping("/{roomId}/leave")
    public ResponseEntity<String> leaveRoom(@PathVariable("roomId") Long roomId) {
        try {
            roomService.removeUserFromRoom(roomId);
            return ResponseEntity.ok("퇴장 완료");
        } catch (RoomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 방을 찾을 수 없습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("알 수 없는 오류가 발생했습니다.");
        }
    }

    @GetMapping("/{roomId}/participants")
    public ResponseEntity<List<ParticipantInfo>> getRoomParticipants(@PathVariable("roomId") Long roomId) {
        try {
            List<ParticipantInfo> participants = roomService.getRoomParticipants(roomId);
            return ResponseEntity.ok(participants);
        } catch (RoomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    @PostMapping("/{roomId}/ready")
    public ResponseEntity<String> setReady(@PathVariable("roomId") Long roomId) {
        User currentUser = roomService.getCurrentUser();
        roomService.updateParticipantStatus(roomId, currentUser.getId(), ParticipantStatus.READY);
        return ResponseEntity.ok("준비 완료");
    }

    @PostMapping("/{roomId}/start")
    public ResponseEntity<String> startRoom(@PathVariable("roomId") Long roomId) {
        User currentUser = roomService.getCurrentUser();
        String message = roomService.startRoom(roomId, currentUser);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    public ApiResponse<List<ScoreDTO>> getRoomScore(@PathVariable("roomId") Long roomId, @RequestParam("problemId") Long problemId) {
        List<ScoreDTO> roundScore = scoreService.getRoundScore(roomId, problemId);
        return makeAPIResponse(Collections.singletonList(roundScore));

    }
}
