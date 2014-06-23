package com.jf.javafx.plugins;



import com.jf.javafx.plugins.impl.ResourceException;
import com.jf.javafx.plugins.impl.datamodels.Resource;
import net.xeoh.plugins.base.Plugin;

/**
 *
 * @author Hoàng Doãn
 */


public interface ResourceRepository extends Plugin {
    public void delete(Resource r) throws ResourceException;
    
    public void upload(Resource r) throws ResourceException;
    
    public void deploy(Resource r, String toPath) throws Exception;
    
    public void undeploy(Resource r) throws ResourceException;
}
