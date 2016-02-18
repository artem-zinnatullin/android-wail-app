### DEPRECATED
This project is no longer maintained.

##Android WAIL Beta — nice last.fm scrobbler
**WAIL — What am I listening** 

*Android [last.fm](http://last.fm) scrobbler and "now playing" updater*

[![Get WAIL on the Google Play!](http://developer.android.com/images/brand/en_generic_rgb_wo_60.png)](https://play.google.com/store/apps/details?id=com.artemzin.android.wail&referrer=utm_source%3Dgithub)

*Main* ***master*** branch build status [on Travis CI](https://travis-ci.org/artem-zinnatullin/android-wail-app): [![Build Status](https://travis-ci.org/artem-zinnatullin/android-wail-app.svg?branch=master)](https://travis-ci.org/artem-zinnatullin/android-wail-app)

[![Join the chat at https://gitter.im/artem-zinnatullin/android-wail-app](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/artem-zinnatullin/android-wail-app?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

**How it looks:**

<img src="screenshots/dark_main.png" alt="Main screen" height="400px"/>
&nbsp;<img src="screenshots/dark_tracks.png" alt="Captured tracks list" height="400px"/>
&nbsp;<img src="screenshots/dark_settings.png" alt="Settings screen" height="400px"/>

<img src="screenshots/light_main.png" alt="Main screen" height="400px"/>
&nbsp;<img src="screenshots/light_tracks.png" alt="Captured tracks list" height="400px"/>
&nbsp;<img src="screenshots/light_settings.png" alt="Settings screen" height="400px"/>

##Main features:
* Scrobbling tracks to the last.fm (even if you are offline, WAIL will send them later)
* Updating #nowplaying
* Sound notifications
* Status bar notifications
* "Love" track
* Option to ignore any player
* Light and dark theme
* English, German and Russian languages

-------------------
###BEFORE CONTRIBUTING TO THE WAIL APP!
Please read small wiki about commits style guides, git work flow and sources styleguides -> [WIKI](https://github.com/artem-zinnatullin/android-wail-app/wiki)  


-------------------
Questions and answers:

* What about code quality & architecture? — Code is pretty old and bad, please take a look at issues list with proposed improvements.
* Why repo has small amount of commits? — Because original repo has my personal data, which I decided to remove before making WAIL Open Source. I decided to not use git filter-branch or bfg to delete these files, so I just created new repo with source code. Sorry guys, about ~250 commits were lost...
* Why you have last.fm secret api keys in Open Source project? — Just because it's very easy to decompile the apk and get them from it, so, please do not use them for evil purposes :)
* Will WAIL be released to Google Play with your fixes? — YES, of course! This is the main purpose of making WAIL Open Source project, I have no time to work on it, so I hope you will help WAIL!
