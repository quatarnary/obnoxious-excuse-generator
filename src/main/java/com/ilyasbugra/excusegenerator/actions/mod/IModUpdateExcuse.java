package com.ilyasbugra.excusegenerator.actions.mod;

import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.model.User;

public interface IModUpdateExcuse {

    default boolean updateExcuse(Excuse excuse, User mod) {
        if (!excuse.getCreatedBy().getId().equals(mod.getId())) {
            return false;
        }
        excuse.setUpdatedBy(mod);
        excuse.setApprovedBy(null);
        return true;
    }
}
