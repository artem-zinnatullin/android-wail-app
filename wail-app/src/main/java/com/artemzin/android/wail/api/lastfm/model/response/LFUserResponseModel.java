package com.artemzin.android.wail.api.lastfm.model.response;

import android.text.TextUtils;

import com.artemzin.android.wail.api.lastfm.LFApiException;

import org.json.JSONException;
import org.json.JSONObject;

public class LFUserResponseModel extends LFBaseResponseModel {

    private String id;
    private String name;
    private String realName;
    private String url;
    private String country;
    private String age;
    private String gender;
    private int    subscriber;
    private int    playCount;
    private int    playlists;
    private Registered registered;

    public static class Registered {
        private String text;
        private long   unixtime;

        private Registered() {}

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public long getUnixtime() {
            return unixtime;
        }

        public void setUnixtime(long unixtime) {
            this.unixtime = unixtime;
        }

        public static Registered fromJSON(JSONObject json) {
            try {
                Registered registered = new Registered();

                registered.text = json.optString("#text");
                registered.unixtime = json.optLong("unixtime");

                return registered;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof Registered) {
                Registered o = (Registered) other;

                return TextUtils.equals(text, o.text)
                        && unixtime == o.unixtime;
            }

            return false;
        }

        @Override
        public int hashCode() {
            int result = text != null ? text.hashCode() : 0;
            result = 31 * result + (int) (unixtime ^ (unixtime >>> 32));
            return result;
        }
    }

    protected LFUserResponseModel(String json) throws LFApiException {
        super(json);
    }

    public static LFUserResponseModel parseFromJSON(String json) throws LFApiException {
        try {
            final LFUserResponseModel userModel = new LFUserResponseModel(json);
            final JSONObject userJson = new JSONObject(json).getJSONObject("user");

            userModel.id = userJson.optString("id");
            userModel.name = userJson.optString("name");
            userModel.realName = userJson.optString("realname");
            userModel.url = userJson.optString("url");
            userModel.country = userJson.optString("country");
            userModel.age = userJson.optString("age");
            userModel.gender = userJson.optString("gender");
            userModel.subscriber = userJson.optInt("subscriber");
            userModel.playCount = userJson.optInt("playcount");
            userModel.playlists = userJson.optInt("playlists");
            userModel.registered = Registered.fromJSON(userJson.optJSONObject("registered"));

            return userModel;
        } catch (JSONException e) {
            throw LFApiException.newDataFormatErrorInstance(null, e.getMessage());
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(int subscriber) {
        this.subscriber = subscriber;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public int getPlaylists() {
        return playlists;
    }

    public void setPlaylists(int playlists) {
        this.playlists = playlists;
    }

    public Registered getRegistered() {
        return registered;
    }

    public void setRegistered(Registered registered) {
        this.registered = registered;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof LFUserResponseModel) {
            LFUserResponseModel o = (LFUserResponseModel) other;

            return TextUtils.equals(id, o.id)
                    && TextUtils.equals(name, o.name)
                    && TextUtils.equals(realName, o.realName)
                    && TextUtils.equals(url, o.url)
                    && TextUtils.equals(country, o.country)
                    && TextUtils.equals(age, o.age)
                    && TextUtils.equals(gender, o.gender)
                    && subscriber == o.subscriber
                    && playCount == o.playCount
                    && playlists == o.playlists
                    && registered.equals(o.registered);
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (realName != null ? realName.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (age != null ? age.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + subscriber;
        result = 31 * result + playCount;
        result = 31 * result + playlists;
        result = 31 * result + (registered != null ? registered.hashCode() : 0);
        return result;
    }
}
