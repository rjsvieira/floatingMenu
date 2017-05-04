package rjsv.floatingmenu.animation.enumerators;

/**
 * Description
 *
 * @author <a href="mailto:ricardo.vieira@xpand-it.com">RJSV</a>
 * @version $Revision : 1 $
 */

public enum AnimationType {

    EXPAND("expand"),
    RADIAL("radial");

    private final String type;

    AnimationType(String type) {
        this.type = type;
    }

    public static AnimationType match(String type) {
        if (type != null) {
            switch (type) {
                case "radial":
                    return RADIAL;
                case "expand":
                default:
                    return EXPAND;
            }
        }
        return EXPAND;
    }

}