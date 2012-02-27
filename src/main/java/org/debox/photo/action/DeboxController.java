package org.debox.photo.action;

import org.debox.photo.server.renderer.JacksonRenderJsonImpl;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class DeboxController extends WebMotionController {

    @Override
    public Render renderJSON(Object... model) {
        return new JacksonRenderJsonImpl(toMap(model));
    }
    
}
