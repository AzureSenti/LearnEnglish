package com.nhom2.learnenglish.core.util

import android.app.Activity
import android.widget.LinearLayout
import com.nhom2.learnenglish.R
import com.nhom2.learnenglish.feature.articles.ArticlesActivity
import com.nhom2.learnenglish.feature.mainmenu.MainMenuActivity
import com.nhom2.learnenglish.feature.wordsets.LibraryActivity
import com.nhom2.learnenglish.ui.activity.ProfileActivity

enum class BottomNavTab {
    EXPLORE,
    LIBRARY,
    LEARN,
    PROFILE
}

object BottomNavigationHelper {

  @JvmStatic
  fun setup(activity: Activity, currentTab: BottomNavTab) {
    activity.findViewById<LinearLayout>(R.id.nav_explore)?.setOnClickListener {
      if (currentTab != BottomNavTab.EXPLORE) {
        Navigator.navigateTo(activity, ArticlesActivity::class.java)
      }
    }

    activity.findViewById<LinearLayout>(R.id.nav_library)?.setOnClickListener {
      if (currentTab != BottomNavTab.LIBRARY) {
        Navigator.navigateTo(activity, LibraryActivity::class.java)
      }
    }

    activity.findViewById<LinearLayout>(R.id.nav_learn)?.setOnClickListener {
      if (currentTab != BottomNavTab.LEARN) {
        Navigator.navigateTo(activity, MainMenuActivity::class.java)
      }
    }

    activity.findViewById<LinearLayout>(R.id.nav_profile)?.setOnClickListener {
      if (currentTab != BottomNavTab.PROFILE) {
        Navigator.navigateTo(activity, ProfileActivity::class.java)
      }
    }
  }
}
