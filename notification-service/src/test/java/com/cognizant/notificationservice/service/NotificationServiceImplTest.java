package com.cognizant.notificationservice.service;

import com.cognizant.notificationservice.domain.Notification;
import com.cognizant.notificationservice.domain.NotificationType;
import com.cognizant.notificationservice.dto.NotificationResponse;
import com.cognizant.notificationservice.dto.SendNotificationRequest;
import com.cognizant.notificationservice.mapper.NotificationMapper;
import com.cognizant.notificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification notification;
    private NotificationResponse notificationResponse;

    @BeforeEach
    void setUp() {
        notification = Notification.builder()
                .id(1L)
                .userId(10L)
                .title("Test Title")
                .message("Test Message")
                .type(NotificationType.GENERAL)
                .isRead(false)
                .createdAt(Instant.now())
                .build();

        notificationResponse = new NotificationResponse();
        notificationResponse.setId(1L);
        notificationResponse.setTitle("Test Title");
        notificationResponse.setMessage("Test Message");
        notificationResponse.setType(NotificationType.GENERAL);
        notificationResponse.setRead(false);
    }

    // -------------------- SEND NOTIFICATION --------------------

    @Test
    void testSend_success() {
        SendNotificationRequest request = new SendNotificationRequest();
        request.setUserId(10L);
        request.setTitle("Test Title");
        request.setMessage("Test Message");
        request.setType(NotificationType.GENERAL);

        when(notificationRepository.save(any(Notification.class)))
                .thenReturn(notification);

        when(notificationMapper.toResponse(notification))
                .thenReturn(notificationResponse);

        NotificationResponse response = notificationService.send(request);

        assertNotNull(response);
        assertEquals("Test Title", response.getTitle());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    // -------------------- GET ALL NOTIFICATIONS --------------------

    @Test
    void testGetAll_success() {
        when(notificationRepository
                .findByUserIdOrderByCreatedAtDesc(10L))
                .thenReturn(List.of(notification));

        when(notificationMapper.toResponse(notification))
                .thenReturn(notificationResponse);

        List<NotificationResponse> responses =
                notificationService.getAll(10L);

        assertEquals(1, responses.size());
        assertEquals("Test Title", responses.get(0).getTitle());
    }

    // -------------------- GET UNREAD NOTIFICATIONS --------------------

    @Test
    void testGetUnread_success() {
        when(notificationRepository
                .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(10L))
                .thenReturn(List.of(notification));

        when(notificationMapper.toResponse(notification))
                .thenReturn(notificationResponse);

        List<NotificationResponse> responses =
                notificationService.getUnread(10L);

        assertEquals(1, responses.size());
        assertFalse(responses.get(0).isRead());
    }

    // -------------------- MARK AS READ --------------------

    @Test
    void testMarkAsRead_success() {
        when(notificationRepository.findById(1L))
                .thenReturn(Optional.of(notification));

        when(notificationRepository.save(notification))
                .thenReturn(notification);

        when(notificationMapper.toResponse(notification))
                .thenReturn(notificationResponse);

        NotificationResponse response =
                notificationService.markAsRead(1L);

        assertNotNull(response);
        verify(notificationRepository, times(1)).save(notification);
        assertTrue(notification.isRead());
    }

    // -------------------- MARK ALL AS READ --------------------

    @Test
    void testMarkAllAsRead_success() {
        when(notificationRepository
                .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(10L))
                .thenReturn(List.of(notification));

        notificationService.markAllAsRead(10L);

        assertTrue(notification.isRead());
        verify(notificationRepository, times(1))
                .saveAll(anyList());
    }
}