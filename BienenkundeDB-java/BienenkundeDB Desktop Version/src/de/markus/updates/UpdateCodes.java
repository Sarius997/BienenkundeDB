package de.markus.updates;

public enum UpdateCodes{
	malformedLink(-2), downloadFailed(-1), updateOk(0), corruptedDownload(1), fuckingHackerLeaveMyPrivateMethodsAlone(404);
			
    private final int id;
    UpdateCodes(int id) { this.id = id; }
    public int getValue() { return id; }
}