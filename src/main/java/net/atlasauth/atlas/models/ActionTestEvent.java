package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/** The sample event a dry-run runs against; every field has a stand-in default. */
public final class ActionTestEvent {
    public User user;
    public Connection connection;
    public Request request;

    public ActionTestEvent() {
    }

    public ActionTestEvent user(User v) { this.user = v; return this; }
    public ActionTestEvent connection(Connection v) { this.connection = v; return this; }
    public ActionTestEvent request(Request v) { this.request = v; return this; }

    public static final class User {
        public String id;
        public String email;
        @JsonProperty("emailVerified")
        public Boolean emailVerified;
        public Map<String, Object> metadata;

        public User() {
        }

        public User id(String v) { this.id = v; return this; }
        public User email(String v) { this.email = v; return this; }
        public User emailVerified(Boolean v) { this.emailVerified = v; return this; }
        public User metadata(Map<String, Object> v) { this.metadata = v; return this; }
    }

    public static final class Connection {
        public String strategy;

        public Connection() {
        }

        public Connection strategy(String v) { this.strategy = v; return this; }
    }

    public static final class Request {
        public String ip;
        public String userAgent;

        public Request() {
        }

        public Request ip(String v) { this.ip = v; return this; }
        public Request userAgent(String v) { this.userAgent = v; return this; }
    }
}
