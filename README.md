##Android WAIL Beta — nice last.fm scrobbler
**WAIL — What am I listening** 

*Android [last.fm](http://last.fm) scrobbler and "now playing" updater*

[![Get WAIL on the Google Play!](http://developer.android.com/images/brand/en_generic_rgb_wo_60.png)](https://play.google.com/store/apps/details?id=com.artemzin.android.wail&referrer=utm_source%3Dgithub)


*Main* ***develop*** branch build status [![Build Status](https://travis-ci.org/artem-zinnatullin/android-wail-app.svg?branch=develop)](https://travis-ci.org/artem-zinnatullin/android-wail-app)

**How it looks:**

<img src="screenshots/screenshot_1.png" alt="Main screen" height="500px"/>
&nbsp;<img src="screenshots/screenshot_2.png" alt="Captured tracks list" height="500px"/>

<img src="screenshots/screenshot_3.png" alt="Settings screen" height="500px"/>
&nbsp;<img src="screenshots/screenshot_4.png" alt="Sound notifications" height="500px"/>

##Main features:
* Scrobbling tracks to the last.fm (even if you are offline, WAIL will send them later)
* Updating #nowplaying
* Sound notifications

-------------------
###BEFORE CONTRIBUTING TO THE WAIL APP!
Please read small wiki about commits style guides, git work flow and sources styleguides -> [WIKI](https://github.com/artem-zinnatullin/android-wail-app/wiki)  


-------------------
Questions and answers:

* Why repo has small amount of commits? — Because original repo has my personal data, which I decided to remove before making WAIL Open Source. I decided to not use git filter-branch or bfg to delete these files, so I just created new repo with source code. Sorry guys, about ~250 commits losted...
* Why you have last.fm secret api keys in Open Source project? — Just because it's very easy to decompile the apk and get them from it, so, please do not use them for evil purposes :)
* Will WAIL be released to Google Play with your fixes? — YES, of course! This is the main purpose of making WAIL Open Source project, I have no time to work on it, so I hope you will help WAIL!
