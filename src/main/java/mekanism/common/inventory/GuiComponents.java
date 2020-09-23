package mekanism.common.inventory;


import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GuiComponents {

    public interface IDropdownEnum<TYPE extends Enum<TYPE> & IDropdownEnum<TYPE>> {

        Text getShortName();

        Text getTooltip();

        default Identifier getIcon() {
            return null;
        }
    }

    public interface IToggleEnum<TYPE extends Enum<TYPE> & IToggleEnum<TYPE>> {

        Text getTooltip();

        Identifier getIcon();
    }
}
