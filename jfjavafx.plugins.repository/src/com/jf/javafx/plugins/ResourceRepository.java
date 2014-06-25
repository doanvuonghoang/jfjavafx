package com.jf.javafx.plugins;



import com.jf.javafx.plugins.impl.datamodels.Resource;
import net.xeoh.plugins.base.Plugin;

/**
 *
 * @author Hoàng Doãn
 */


public interface ResourceRepository extends Plugin {
    public void delete(Resource r) throws Exception;
    
    public void deletePluginResource(long pluginId) throws Exception;
    
    public void save(Resource r) throws Exception;
    
    public void deploy(Resource r, String toPath) throws Exception;
    
    public void undeploy(Resource r) throws Exception;
}
