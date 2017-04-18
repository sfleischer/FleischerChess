import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Art {

	public static BufferedImage findImage(String file){
        BufferedImage image = null;
        try{
            image = ImageIO.read(new File(file));
        } catch(IOException e){
            System.out.println(file+ " was not found");
        } 
        return image;
    }
}
