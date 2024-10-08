package ch.heig.dai.lab.fileio;

import java.io.File;
import java.nio.charset.Charset;

import ch.heig.dai.lab.fileio.Fullann.*;

public class Main {
    private static final String newName = "Nathan Füllemann - Fullann";

    /**
     * Main method to transform files in a folder.
     * Create the necessary objects (FileExplorer, EncodingSelector, FileReaderWriter, Transformer).
     * In an infinite loop, get a new file from the FileExplorer, determine its encoding with the EncodingSelector,
     * read the file with the FileReaderWriter, transform the content with the Transformer, write the result with the
     * FileReaderWriter.
     *
     * Result files are written in the same folder as the input files, and encoded with UTF8.
     *
     * File name of the result file:
     * an input file "myfile.utf16le" will be written as "myfile.utf16le.processed",
     * i.e., with a suffixe ".processed".
     */
    public static void main(String[] args) {
        // Read command line arguments
        if (args.length != 2 || !new File(args[0]).isDirectory()) {
            System.out.println("You need to provide two command line arguments: an existing folder and the number of words per line.");
            System.exit(1);
        }
        String folder = args[0];
        int wordsPerLine = Integer.parseInt(args[1]);

        // Create the necessary objects
        FileExplorer fileExplorer = new FileExplorer(folder);
        EncodingSelector encodingSelector = new EncodingSelector();
        FileReaderWriter fileReaderWriter = new FileReaderWriter();
        Transformer transformer = new Transformer(newName, wordsPerLine);

        System.out.println("Application started, reading folder " + folder + "...");

        // Infinite loop to process files
        while (true) {
            try {
                // Get a new file from the FileExplorer
                File file = fileExplorer.getNewFile();
                if (file == null) {
                    // No new file to process, break the loop
                    System.out.println("No new files to process. Exiting...");
                    break;
                }

                // Determine the encoding of the file
                Charset encoding = encodingSelector.getEncoding(file);
                if (encoding == null) {
                    System.out.println("Unknown file encoding for file: " + file.getName());
                    continue;
                }

                // Read the file content
                String content = fileReaderWriter.readFile(file, encoding);
                if (content == null) {
                    System.out.println("Failed to read file: " + file.getName());
                    continue;
                }

                // Transform the content
                String transformedContent = transformer.replaceChuck(content);
                transformedContent = transformer.capitalizeWords(transformedContent);
                transformedContent = transformer.wrapAndNumberLines(transformedContent);

                // Prepare the output file name (add ".processed" suffix)
                String outputFileName = file.getName() + ".processed";
                File outputFile = new File(file.getParent(), outputFileName);

                // Write the transformed content to the output file with UTF-8 encoding
                boolean success = fileReaderWriter.writeFile(outputFile, transformedContent, Charset.forName("UTF-8"));
                if (success) {
                    System.out.println("Processed file written to: " + outputFile.getName());
                } else {
                    System.out.println("Failed to write the processed file: " + outputFile.getName());
                }

            } catch (Exception e) {
                System.out.println("Exception: " + e);
            }
        }
    }
}
