package com.ilyasbugra.excusegenerator.v2.actions.admin;

import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.model.User;

public interface IAdminUpdateExcuse {

    default boolean updateExcuse(Excuse excuse, User admin) {
        excuse.setUpdatedBy(admin);
        excuse.setApprovedBy(admin);
        return true;
    }
}
