package com.github.ojvzinn.skinchecker;

import com.github.ojvzinn.skinchecker.interfaces.ImageAction;
import com.github.ojvzinn.skinchecker.utils.PixeisLocation;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {

    private static final Set<PixeisLocation> wrongPixels = new HashSet<>();
    private static boolean isWrong;

    @SneakyThrows
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        BufferedImage image = getSelectedImage(sc);
        if (image == null) {
            return;
        }

        setupWrongLocations();
        if (containsWrongPixel(image)) {
            System.out.println("Essa imagem está incorreta!");
            return;
        }

        System.out.println("Sua imagem está aprovada!");
    }

    @SneakyThrows
    private static void setupWrongLocations() {
        BufferedImage imageBase = getImage("./pixeisCertos.png");
        if (imageBase == null) {
            throw new RuntimeException();
        }

        checkImage((width, height) -> {
            Color color = new Color(imageBase.getRGB(width, height), true);
            if (color.getRGB() == 0) {
                wrongPixels.add(new PixeisLocation(width, height));
            }
        }, imageBase);
    }

    private static boolean containsWrongPixel(BufferedImage image) {
        isWrong = false;
        checkImage((width, height) -> {
            Color color = new Color(image.getRGB(width, height), true);
            if (color.getRGB() != 0 && isWrongPixel(width, height)) {
                isWrong = true;
            }

            if (isIsWrongAlpha(color)) {
                isWrong = true;
            }
        }, image);
        return isWrong;
    }

    private static BufferedImage getSelectedImage(Scanner sc) {
        System.out.println("Digite o patch do arquivo: ");
        return getImage(sc.nextLine());
    }

    private static boolean isWrongPixel(int x, int y) {
        return wrongPixels.stream().anyMatch(pixeisLocation -> pixeisLocation.getX() == x && pixeisLocation.getY() == y);
    }

    private static boolean isIsWrongAlpha(Color color) {
        return color.getAlpha() != 0 && color.getAlpha() != 255;
    }

    private static BufferedImage getImage(String filePatch) {
        File file = new File(filePatch);
        if (!file.exists()) {
            System.out.println("Arquivo não encontrado!");
            return null;
        }

        if (!file.getName().endsWith(".png")) {
            System.out.println("Arquivo no fortato errado!");
            return null;
        }

        BufferedImage image;
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            System.out.println("Não foi possível tranformar o arquivo em uma imagem compativél!");
            throw new RuntimeException(e);
        }

        return image;
    }

    private static void checkImage(ImageAction action, BufferedImage image) {
        for (int width = 0; width < image.getWidth(); width++) {
            for (int height = 0; height < image.getHeight(); height++) {
                action.action(width, height);
            }
        }
    }
}