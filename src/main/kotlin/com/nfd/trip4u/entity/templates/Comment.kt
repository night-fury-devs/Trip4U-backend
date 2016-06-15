package com.nfd.trip4u.entity.templates

import org.springframework.stereotype.Component
import java.util.*

/**
 * Author: Mary Kuchumova
 * Date: 12 June 2016
 * Time: 16:02
 */

@Component
data class Comment(
        var name: String,
        var date: Date,
        var text: String
        )