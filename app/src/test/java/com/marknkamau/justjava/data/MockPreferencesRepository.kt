package com.marknkamau.justjava.data

import com.marknkamau.justjava.models.UserDefaults

class MockPreferencesRepository : PreferencesRepository {
    override fun saveDefaults(userDefaults: UserDefaults) {

    }

    override fun getDefaults(): UserDefaults {
        return UserDefaults("name", "phone", "address")
    }

    override fun clearDefaults() {

    }

}
