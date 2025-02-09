package com.ilyasbugra.excusegenerator.actions.admin;

import org.springframework.stereotype.Component;

@Component
public class AdminUser implements IAdminCreateExcuse, IAdminDeleteExcuse, IAdminApproveExcuse, IAdminUpdateExcuse {
    // All Logic handled via Interfaces for now.
}
