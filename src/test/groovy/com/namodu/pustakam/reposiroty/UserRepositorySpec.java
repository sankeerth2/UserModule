//package com.namodu.pustakam.reposiroty;
//
//import com.namodu.pustakam.model.Invitation;
//import com.namodu.pustakam.model.RequestContext;
//import com.namodu.pustakam.model.User;
//import com.namodu.pustakam.repository.InvitationRepository;
//import com.namodu.pustakam.repository.UserRepository;
//import com.namodu.pustakam.repository.UserRoleRepository;
//import com.namodu.pustakam.repository.impl.UserRepositoryImpl;
//import com.namodu.pustakam.utilities.rowMappers.UserRowMapper;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//
///**
// * Created by sanemdeepak on 11/20/16.
// */
//public class UserRepositorySpec {
//
//    @Mock
//    UserRoleRepository userRoleRepository;
//
//    @Mock
//    InvitationRepository invitationRepository;
//
//    @Mock
//    UserRowMapper userRowMapper;
//
//    @Mock
//    JdbcTemplate jdbcTemplate;
//
//    @Mock
//    Invitation invitation;
//
//    @Mock
//    UserRepository userRepository;
//
//    @Mock
//    User user;
//
//    @Mock
//    RequestContext requestContext;
//
//    @InjectMocks
//    @Autowired
//    UserRepositoryImpl userRepositoryImpl;
//
//    @Before
//    public void initMocks() {
//        MockitoAnnotations.initMocks(this);
//    }
//
//    @Test
//    public void testUserCreatedIfAllRequiredFieldsAreSet() {
//        //setup
//        Mockito.doReturn("1234").when(invitation).getInvitationId();
//        Mockito.doReturn(invitation).when(invitationRepository).findInvitationByUserLinkId("user-link-id");
//        Mockito.doReturn("login").when(user).getLogin();
//        Mockito.doReturn("password").when(user).getPassword();
//        Mockito.doReturn("test").when(user).getFirstName();
//        Mockito.doReturn("last").when(user).getLastName();
//        Mockito.doReturn("org1").when(user).getUserOrg();
//        Mockito.doReturn("email").when(user).getEmail();
//        Mockito.doReturn("12345").when(user).getMobile();
//        Mockito.doReturn(0).when(user).getActive();
//        Mockito.doReturn(1).when(user).getNewUser();
//        Mockito.doReturn("user-link-id").when(user).getUserLinkId();
//        Mockito.doReturn("add-ed-by").when(user).getAddedBy();
//        Mockito.doReturn("lastpdated").when(user).getLastUpdatedBy();
//
//        //test method
//        invitation = userRepositoryImpl.create(user);
//
//        //expect
//        Mockito.verify(invitationRepository, Mockito.times(1)).createNewInvitation(Mockito.any());
//        Mockito.verify(invitationRepository, Mockito.times(1)).findInvitationByUserLinkId(Mockito.any());
//
//        Assert.assertEquals(invitation.getInvitationId(), "1234");
//    }
//
//    @Test(expected = NullPointerException.class)
//    public void testNullPointerExceptionThrownIfanyRequiredFieldNotSet() {
//        //missing firstname, lastname , mobile and email
//        Mockito.doReturn("login").when(user).getLogin();
//        Mockito.doReturn("password").when(user).getPassword();
//        Mockito.doReturn(0).when(user).getActive();
//        Mockito.doReturn(1).when(user).getNewUser();
//        Mockito.doReturn("user-link-id").when(user).getUserLinkId();
//        Mockito.doReturn("add-ed-by").when(user).getAddedBy();
//        Mockito.doReturn("lastpdated").when(user).getLastUpdatedBy();
//
//        //test method
//        invitation = userRepositoryImpl.create(requestContext, user);
//    }
//
//
//    @Test
//    public void testWhenExceptionThrownCatchBlockisExecuted() throws Exception{
//
//
//        //test method
//        try {
//            userRepositoryImpl.create(Mockito.any(), Mockito.any());
//        }catch (Exception e){
//            Mockito.verify(userRepository, Mockito.times(1)).deleteUserByUserLinkId(Mockito.any(), Mockito.any());
//        }
//    }
//
//}
