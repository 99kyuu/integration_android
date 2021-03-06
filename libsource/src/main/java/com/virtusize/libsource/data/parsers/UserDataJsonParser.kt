package com.virtusize.libsource.data.parsers

import com.virtusize.libsource.data.remote.UserData
import org.json.JSONObject

/**
 * This class parses a JSONObject to the [UserData] object
 */
internal class UserDataJsonParser: VirtusizeJsonParser {
    override fun parse(json: JSONObject): UserData? {
        val shouldSeePhTooltip = json.optBoolean(FIELD_SHOULD_SEE_PH_TOOLTIP)
        val wardrobeHasP = json.optBoolean(FIELD_WARDROBE_HAS_P)
        val wardrobeHasR = json.optBoolean(FIELD_WARDROBE_HAS_R)
        val wardrobeHasM = json.optBoolean(FIELD_WARDROBE_HAS_M)
        val wardrobeActive = json.optBoolean(FIELD_WARDROBE_ACTIVE)
        return UserData(shouldSeePhTooltip, wardrobeHasP, wardrobeHasR, wardrobeHasM, wardrobeActive)
    }

    companion object {
        private const val FIELD_SHOULD_SEE_PH_TOOLTIP = "should_see_ph_tooltip"
        private const val FIELD_WARDROBE_HAS_P = "wardrobeHasP"
        private const val FIELD_WARDROBE_HAS_R = "wardrobeHasR"
        private const val FIELD_WARDROBE_HAS_M = "wardrobeHasM"
        private const val FIELD_WARDROBE_ACTIVE = "wardrobeActive"
    }
}