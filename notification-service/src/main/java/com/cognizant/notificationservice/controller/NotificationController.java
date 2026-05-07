//package com.cognizant.notificationservice.controller;
//
//import com.cognizant.notificationservice.domain.Notification;
//import com.cognizant.notificationservice.dto.NotificationResponse;
//import com.cognizant.notificationservice.dto.SendNotificationRequest;
//import com.cognizant.notificationservice.service.NotificationService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/notifications")
//@RequiredArgsConstructor
//@Tag(name = "Notification Service", description = "APIs for managing notifications in the hospital management system")
//public class NotificationController {
//
//    private final NotificationService notificationService;
//
//    @GetMapping("/my/messages")
//    @Operation(summary = "Get all notifications for a user", description = "Returns all notifications for the specified user ID.")
//    public ResponseEntity<List<NotificationResponse>> getAll(@RequestHeader("X-User-Id") Long id) {
//        List<NotificationResponse> response =  notificationService.getAll(id);
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }
//
//    @PostMapping("/send")
//    public ResponseEntity<NotificationResponse> send(@Valid @RequestBody SendNotificationRequest request) {
//        NotificationResponse response = notificationService.send(request);
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//
//    @GetMapping("/my/unread")
//    @Operation(summary = "Get unread notifications for logged-in user")
//    public ResponseEntity<List<NotificationResponse>> getUnread(@RequestHeader("X-User-Id") Long userId) {
//        List<NotificationResponse> response = notificationService.getUnread(userId);
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }
//
//    @PutMapping("/{id}/read")
//    @Operation(summary = "Mark a notification as read")
//    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long id) {
//        NotificationResponse response = notificationService.markAsRead(id);
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }
//
//    @PutMapping("/read-all")
//    @Operation(summary = "Mark all notifications as read")
//    public ResponseEntity<Map<String, String>> markAllAsRead(@RequestHeader("X-User-Id") Long userId) {
//        notificationService.markAllAsRead(userId);
//        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "All notifications marked as read"));
//    }
//
//}
package com.cognizant.notificationservice.controller;

import com.cognizant.notificationservice.dto.NotificationResponse;
import com.cognizant.notificationservice.dto.SendNotificationRequest;
import com.cognizant.notificationservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Service", description = "Public APIs for managing user notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/{userId}/allMessages")
    @Operation(summary = "Get all notifications for a user")
    public ResponseEntity<List<NotificationResponse>> getAll(
            @PathVariable Long userId) {

        List<NotificationResponse> response =
                notificationService.getAll(userId);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/send")
    @Operation(summary = "Send a notification to a user")
    public ResponseEntity<NotificationResponse> send(
            @Valid @RequestBody SendNotificationRequest request) {

        NotificationResponse response =
                notificationService.send(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @GetMapping("/{userId}/unread")
    @Operation(summary = "Get unread notifications for a user")
    public ResponseEntity<List<NotificationResponse>> getUnread(
            @PathVariable Long userId) {

        List<NotificationResponse> response =
                notificationService.getUnread(userId);

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{notificationId}/read")
    @Operation(summary = "Mark a notification as read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable Long notificationId) {

        NotificationResponse response =
                notificationService.markAsRead(notificationId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}/read-all")
    @Operation(summary = "Mark all notifications as read for a user")
    public ResponseEntity<Map<String, String>> markAllAsRead(
            @PathVariable Long userId) {

        notificationService.markAllAsRead(userId);

        return ResponseEntity.ok(
                Map.of("message", "All notifications marked as read")
        );
    }
}