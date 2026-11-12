package com.mrcrayfish.controllable.client.binding;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mrcrayfish.controllable.Controllable;
import com.mrcrayfish.controllable.client.ControllerInput;
import com.mrcrayfish.controllable.client.binding.ButtonBinding;
import com.mrcrayfish.controllable.platform.ClientServices;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

/**
 * A special binding that translates button presses to key presses. This binding does not need to be
 * registered and is added by players during runtime.
 *
 * Author: MrCrayfish
 */
public final class KeyAdapterBinding extends ButtonBinding
{
    private final KeyMapping keyMapping;
    private final String labelKey;

    public KeyAdapterBinding(int button, KeyMapping mapping)
    {
        super(button, mapping.getName() + ".custom", "key.categories.controllable_custom", ClientServices.CLIENT.createBindingContext(mapping));
        this.keyMapping = mapping;
        this.labelKey = mapping.getName();
    }

    @Override
    public String getLabelKey()
    {
        return this.labelKey;
    }

    public KeyMapping getKeyMapping()
    {
        return this.keyMapping;
    }

    @Override
    protected void setPressed(boolean pressed)
    {
        super.setPressed(pressed);
        this.keyMapping.setDown(pressed);
        if(pressed) this.updateKeyBindPressTime();
        int key = ClientServices.CLIENT.getKeyValue(this.keyMapping);
        this.handlePressed(pressed ? GLFW.GLFW_PRESS : GLFW.GLFW_RELEASE, key, 0);
    }

    @Override
    protected void onPressTick()
    {
        //this.updateKeyBindPressTime();
    }

    private void updateKeyBindPressTime()
    {
        ClientServices.CLIENT.setKeyPressTime(this.keyMapping, 1);
    }

    private void handlePressed(int action, int key, int modifiers)
    {
        Screen screen = Minecraft.getInstance().screen;
        if(InputConstants.getKey(this.keyMapping.saveString()).getType() == InputConstants.Type.MOUSE)
        {
            // A mapping on a mouse button (e.g. a middle click) is a click at the cursor, its value is no key code
            if(screen != null)
            {
                this.sendMouseButton(screen, action, key);
            }
            return;
        }
        if(screen != null)
        {
            if(ClientServices.CLIENT.sendScreenInput(screen, key, action, modifiers))
            {
                return;
            }
        }
        ClientServices.CLIENT.sendKeyInputEvent(key, 0, action, modifiers);
    }

    private void sendMouseButton(Screen screen, int action, int button)
    {
        Minecraft mc = Minecraft.getInstance();
        ControllerInput input = Controllable.getInput();
        Window window = mc.getWindow();
        double mouseX = input.isVirtualCursorActive() ? input.getScaledCursorX() : mc.mouseHandler.xpos() * window.getGuiScaledWidth() / window.getWidth();
        double mouseY = input.isVirtualCursorActive() ? input.getScaledCursorY() : mc.mouseHandler.ypos() * window.getGuiScaledHeight() / window.getHeight();
        if(action == GLFW.GLFW_PRESS)
        {
            ClientServices.CLIENT.sendScreenMouseClick(screen, mouseX, mouseY, button);
        }
        else
        {
            ClientServices.CLIENT.sendScreenMouseReleased(screen, mouseX, mouseY, button);
        }
    }
}
