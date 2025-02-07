package com.ilyasbugra.excusegenerator.v2.actions.admin;

import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.v2.model.User;

public interface IAdminCreateExcuse {

    default void createExcuse(Excuse excuse, User admin) {
        excuse.setCreatedBy(admin);
        excuse.setApprovedBy(admin);
    }
}
