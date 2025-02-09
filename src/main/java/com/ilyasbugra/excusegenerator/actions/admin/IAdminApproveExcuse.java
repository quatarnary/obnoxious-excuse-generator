package com.ilyasbugra.excusegenerator.actions.admin;

import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.model.User;

public interface IAdminApproveExcuse {

    default void approveExcuse(Excuse excuse, User admin) {
        if (excuse.getApprovedBy() == null) {
            excuse.setApprovedBy(admin);
        }
    }
}
