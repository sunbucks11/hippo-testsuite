/*
 *  Copyright 2008 Hippo.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.hippoecm.hst.demo.beans;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSetBean;

@Node(jcrType="demosite:productdocument")
public class ProductBean extends TextBean{
    
    private HippoGalleryImageSetBean imageBean;
    private boolean imagesLoaded = false;
    
    public String getTitle() {
        // product does not have a title
        return this.getLocalizedName();
    }
    
    public String getBrand(){
        return this.getProperty("demosite:brand");
    }
    
    public String getProduct(){
        return this.getProperty("demosite:product");
    }
    
    public String getColor(){
        return this.getProperty("demosite:color");
    }
    
    public String getType(){
        return this.getProperty("demosite:type");
    }
    
    public Double getPrice(){
        return this.getProperty("demosite:price");
    }
    
    public String[] getTags(){
        return this.getProperty("hippostd:tags");
    }
    
    public HippoGalleryImageSetBean getImage() {
        if(imagesLoaded) {
            return this.imageBean;
        }
        imagesLoaded = true;
        this.imageBean = this.getLinkedBean("demosite:image", HippoGalleryImageSetBean.class);
        return imageBean;
    }
    
}
