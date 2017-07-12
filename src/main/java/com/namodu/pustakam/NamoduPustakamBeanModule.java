package com.namodu.pustakam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.namodu.pustakam.utilities.rowMappers.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by sanemdeepak on 9/27/16.
 */
@Configuration
public class NamoduPustakamBeanModule {

    @Bean
    public ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
        return mapper;
    }

    @Bean
    public UserRowMapper createUserRowMapper() {
        return new UserRowMapper();
    }

    @Bean
    public RoleMapper createRoleRowMapper() {
        return new RoleMapper();
    }

    @Bean
    public PermissionMapper createPermissionMapper() {
        return new PermissionMapper();
    }

    @Bean
    public UserRoleMapper createUserRoleMapper() {
        return new UserRoleMapper();
    }

    @Bean
    public InvitationRowMapper createInvitationMapper() {
        return new InvitationRowMapper();
    }
}
