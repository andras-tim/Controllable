package com.mrcrayfish.controllable.client.gui.navigation;

import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.TabButton;

/**
 * Author: MrCrayfish
 */
public class WidgetNavigationPoint extends NavigationPoint
{
    private final AbstractWidget widget;

    public WidgetNavigationPoint(AbstractWidget widget)
    {
        super(widget.getX() + widget.getWidth() / 2.0, widget.getY() + widget.getHeight() / 2.0, Type.WIDGET);
        this.widget = widget;
    }

    public AbstractWidget getWidget()
    {
        return this.widget;
    }

    @Override
    public boolean shouldHide()
    {
        // A checkbox shows keyboard focus only, so the cursor is its only sign of being selected
        if(this.widget instanceof Checkbox)
            return false;
        return this.widget instanceof AbstractButton || this.widget instanceof TabButton || this.widget instanceof HideCursor;
    }
}
