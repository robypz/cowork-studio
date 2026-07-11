package com.orinocolabs.cowork_studio.identity.application.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.orinocolabs.cowork_studio.identity.application.query.profile.GetUserProfileQuery;
import com.orinocolabs.cowork_studio.identity.application.query.profile.GetUserProfileQueryHandler;
import com.orinocolabs.cowork_studio.identity.application.query.profile.UserProfileView;
import com.orinocolabs.cowork_studio.identity.domain.exception.UserNotFoundException;
import com.orinocolabs.cowork_studio.identity.testsupport.InMemoryUserProfileFinder;

class GetUserProfileQueryHandlerTest {

    private final InMemoryUserProfileFinder userProfileFinder = new InMemoryUserProfileFinder();
    private final GetUserProfileQueryHandler handler = new GetUserProfileQueryHandler(userProfileFinder);

    @Test
    void returns_the_profile_of_an_existing_user() {
        UUID userId = UUID.randomUUID();
        userProfileFinder.put(new UserProfileView(userId, "robert@example.com", "CLIENT", true, Instant.now()));

        UserProfileView profile = handler.handle(new GetUserProfileQuery(userId));

        assertEquals(userId, profile.id());
        assertEquals("robert@example.com", profile.email());
        assertEquals("CLIENT", profile.role());
    }

    @Test
    void rejects_an_unknown_user() {
        assertThrows(UserNotFoundException.class, () -> handler.handle(new GetUserProfileQuery(UUID.randomUUID())));
    }
}
