package com.nfd.trip4u.entity.templates

/**
 * Author: Mary Kuchumova
 * Date: 12 June 2016
 * Time: 14:29
 */

open class EmailConfirmationTemplate : BaseTemplate {

    private var confirmationLink: String
        get() = confirmationLink
        set(value) {
            confirmationLink = value
        }

    constructor(websiteLink: String, contactLink: String, username: String,
                confirmationLink: String, logoImageLink: String)
                : super(websiteLink, contactLink, username, logoImageLink) {
        this.confirmationLink = confirmationLink
    }
}