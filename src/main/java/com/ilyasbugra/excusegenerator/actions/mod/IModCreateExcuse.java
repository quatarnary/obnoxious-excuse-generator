package com.ilyasbugra.excusegenerator.actions.mod;

import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.model.User;

public interface IModCreateExcuse {

    default void createExcuse(Excuse excuse, User mod) {
        excuse.setCreatedBy(mod);
    }
}
