import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

    interface IEncoder {
        String encode(String input, int key);

        String decode(String input, int key);
    }

    class ShiftCipher implements IEncoder {
        @Override
        public String encode(String input, int key) {
            StringBuilder output = new StringBuilder();
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                if (Character.isLetter(c)) {
                    c = (char) (input.charAt(i) + key);
                    if ((Character.isLowerCase(input.charAt(i)) && (c > 'z'))
                            || (Character.isUpperCase(input.charAt(i)) && (c > 'Z'))) {
                        c = (char) (input.charAt(i) - (26 - key));
                    }
                }
                output.append(c);
            }
            return output.toString();
        }

        @Override
        public String decode(String input, int key) {
            StringBuilder output = new StringBuilder();
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                if (Character.isLetter(c)) {
                    c = (char) (input.charAt(i) - key);
                    if ((Character.isLowerCase(input.charAt(i)) && c < 'a')
                            || (Character.isUpperCase(input.charAt(i)) && c < 'A')) {
                        c = (char) (input.charAt(i) + (26 - key));
                    }
                }
                output.append(c);
            }
            return output.toString();
        }
    }

    class UnicodeCipher implements IEncoder {
        @Override
        public String encode(String input, int key) {
            StringBuilder result = new StringBuilder();
            for (char c : input.toCharArray()) {
                result.append((char) (c + key));
            }
            return result.toString();
        }

        @Override
        public String decode(String input, int key) {
            StringBuilder result = new StringBuilder();
            for (char c : input.toCharArray()) {
                result.append((char) (c - key));
            }
            return result.toString();
        }
    }

    class EncoderFactory {
        public IEncoder create(String alg) {
            alg = alg.toUpperCase();

            return switch (alg) {
                case "SHIFT" -> new ShiftCipher();
                case "UNICODE" -> new UnicodeCipher();
                default -> throw new IllegalStateException("Unexpected value: " + alg);
            };
        }
    }

    class Solution {
        private String input = "";
        private String algorithm = "";
        private String encode;
        private String data = "";
        private int key = 0;
        private String inFile = "";
        private String inFileData = "";
        private String outFile = "";
        private String output;

        public void run(String[] args) {
            argumentsParse(args);
            inputInFile();
            encodeAndOutput();
        }

        private void argumentsParse(String[] args) {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-mode" -> encode = args[i + 1];
                    case "-data" -> data = args[i + 1];
                    case "-alg" -> algorithm = args[i + 1];
                    case "-key" -> key = Integer.parseInt(args[i + 1]);
                    case "-in" -> inFile = args[i + 1];
                    case "-out" -> outFile = args[i + 1];
                }
            }
        }

        private void inputInFile() {

            if (!this.inFile.isBlank()) {
                try (Scanner scanner = new Scanner(new File(this.inFile))) {
                    this.inFileData = scanner.nextLine();
                } catch (FileNotFoundException e) {
                    System.out.println("File not found");
                }
            }

            this.input = this.data.equals("") ? inFileData : data;
        }

        private void encodeAndOutput() {
            final EncoderFactory encoderFactory = new EncoderFactory();
            final IEncoder encoder = encoderFactory.create(algorithm);

            if (encode.equals("enc")) {
                output = encoder.encode(input, key);
            } else {
                output = encoder.decode(input, key);
            }

            if (outFile.isBlank()) {
                System.out.println(output);
            } else {
                try (FileWriter fileWriter = new FileWriter(outFile)) {
                    fileWriter.write(output);
                    fileWriter.flush();
                } catch (IOException e) {
                    System.out.println("Error writing result");
                }
            }
        }

        public static void main(String[] args) {
            Solution solution = new Solution();
            try {
                solution.run(args);
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }
