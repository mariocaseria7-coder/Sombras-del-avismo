package com.sombrasdelavismo.model;

public record ActionResult(boolean successful, String publicMessage, String privateMessage) {
    public static ActionResult success(String publicMessage) {
        return new ActionResult(true, publicMessage, null);
    }

    public static ActionResult success(String publicMessage, String privateMessage) {
        return new ActionResult(true, publicMessage, privateMessage);
    }

    public static ActionResult failure(String publicMessage) {
        return new ActionResult(false, publicMessage, null);
    }
}
