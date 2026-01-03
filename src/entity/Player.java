package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.io.InputStream;

import main.GamePanel;
import main.KeyHandler;

public class Player extends Entity {

    GamePanel gp;
    KeyHandler keyH;

    public final int screenX;
    public final int screenY;
    int hasKey=0;

    public Player(GamePanel gp, KeyHandler keyH) {

        this.gp = gp;
        this.keyH = keyH;

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        solidArea = new Rectangle();
        solidArea.x = 8;
        solidArea.y = 16;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        solidArea.width = 32;
        solidArea.height = 32;

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        // set player's default position
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 22;
        speed = 4;
        direction = "down";
    }

    private BufferedImage loadImage(String path) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null) {
                throw new IllegalArgumentException("Image not found: " + path);
            }
            return ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getPlayerImage() {
        up1 = loadImage("/res/player/c_up1.png");
        up2 = loadImage("/res/player/c_up2.png");
        down1 = loadImage("/res/player/c_down1.png");
        down2 = loadImage("/res/player/c_down2.png");
        left1 = loadImage("/res/player/c_left1.png");
        left2 = loadImage("/res/player/c_left2.png");
        right1 = loadImage("/res/player/c_right1.png");
        right2 = loadImage("/res/player/c_right2.png");
    }

    public void updateSprite() {
        spriteCounter++;
        if (spriteCounter > 12) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }

    public void update() {

        boolean moving = keyH.upPressed == true || keyH.downPressed == true || keyH.leftPressed == true
                || keyH.rightPressed == true;
        if (moving) {
            if (keyH.upPressed == true) {
                direction = "up";

            } else if (keyH.downPressed == true) {
                direction = "down";

            } else if (keyH.leftPressed == true) {
                direction = "left";

            } else if (keyH.rightPressed == true) {
                direction = "right";

            }
            // check tile collision
            collosionOn = false;
            gp.cChecker.checkTile(this);

            // check object collision such as key door etc
            int objIndex = gp.cChecker.checkObject(this, true);
            pickUpObject(objIndex);

            // if collosion is false, player can move
            if (collosionOn == false) {
                switch (direction) {
                    case "up": worldY -= speed; break;
                    case "down": worldY += speed; break;
                    case "left": worldX -= speed; break;
                    case "right": worldX += speed; break;
                    
                }
            }

            updateSprite();
        }
    }

    public void pickUpObject(int i){
        if(i != 999){
            String objectName = gp.obj[i].name;

            switch (objectName) {
                case "Key":
                    hasKey++;
                    gp.obj[i] = null;
                    System.out.println("Key : "+hasKey);
                    break;

                case "Door":
                    if (hasKey>0) {
                        gp.obj[i] = null;
                        hasKey--;
                    }
                    System.out.println("Key : "+hasKey);
                    break;

                case "Boots":
                    speed += 2;
                    gp.obj[i] = null;
                    break;
            
                default:
                    break;
            }
        }
    }

    public void draw(Graphics2D g2) {

        // g2.setColor(Color.white);
        // g2.fillRect(x, y, gp.tileSize, gp.tileSize);

        BufferedImage image = null;

        switch (direction) {
            case "up":
                image = (spriteNum == 1) ? up1 : up2;
                break;
            case "down":
                image = (spriteNum == 1) ? down1 : down2;
                break;
            case "left":
                image = (spriteNum == 1) ? left1 : left2;
                break;
            case "right":
                image = (spriteNum == 1) ? right1 : right2;
                break;
        }
        if (image == null) {
            // Draw a placeholder if no image is loaded
            g2.setColor(Color.white);
            g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
        } else {
            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        }
    }
}
