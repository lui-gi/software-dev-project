package ems.service;

import ems.error.AccessDeniedException;
import ems.model.User;

/** Role checks. Services call these before any read or change that is role-restricted. */
final class Access {
    private Access() { }

    static void requireHr(User user) {
        if (user == null || !user.isHrAdmin()) throw new AccessDeniedException();
    }
}
