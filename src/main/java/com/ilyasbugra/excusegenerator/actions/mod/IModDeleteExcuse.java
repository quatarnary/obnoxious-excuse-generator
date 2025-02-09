package com.ilyasbugra.excusegenerator.actions.mod;

import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.model.User;

public interface IModDeleteExcuse {

    default boolean deleteExcuse(Excuse excuse, User mod) {
        return excuse.getCreatedBy().getId().equals(mod.getId());
    }
}
