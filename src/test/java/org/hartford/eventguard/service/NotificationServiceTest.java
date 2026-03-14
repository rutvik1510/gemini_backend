package org.hartford.eventguard.service;

import org.hartford.eventguard.entity.Notification;
import org.hartford.eventguard.entity.User;
import org.hartford.eventguard.exception.ResourceNotFoundException;
import org.hartford.eventguard.repo.NotificationRepository;
import org.hartford.eventguard.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock private NotificationRepository notificationRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User user;
    private Notification notification;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@test.com");
        
        notification = new Notification(user, "Test message", "INFO");
        notification.setId(1L);
    }

    @Test
    void createNotification_Success() {
        notificationService.createNotification(user, "New Message", "ALERT");
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void getMyNotifications_Success() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(notificationRepository.findByRecipientOrderByCreatedAtDesc(user)).thenReturn(Arrays.asList(notification));

        List<Notification> results = notificationService.getMyNotifications("test@test.com");

        assertEquals(1, results.size());
        assertEquals("Test message", results.get(0).getMessage());
    }

    @Test
    void markAsRead_Success() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        
        notificationService.markAsRead(1L);
        
        assertTrue(notification.isRead());
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    void markAsRead_NotFound_ThrowsException() {
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> {
            notificationService.markAsRead(99L);
        });
    }
}
