package com.company;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        if(Arrays.toString(args)=="[]") {
            System.err.print("Usage: ciran file");
            return;
        }
        for (String arg : args) {
            List<Element> cir = parseFile(arg);
            if(cir==null) continue;
            int num_max = 0, vol_src = 0;
            for (Element i : cir) {
                System.out.println(i.toString());
                num_max = (i.get_numm()>num_max)?i.get_numm():num_max;
                num_max = (i.get_nump()>num_max)?i.get_nump():num_max;
                vol_src = (Objects.equals(i.get_type(), "V")) ? (vol_src + 1) : vol_src;
            }
            int tmp = num_max + vol_src + 1;
            Matrix A = new Matrix(tmp, tmp);
            Matrix z = new Matrix(tmp, 1);

            //
            String[] head = new String[vol_src+num_max+1];
            int i, j;
            for(i = 0; i < num_max; i++)
                head[i]=String.format("v%d", i);
            for(j = 1; i <= vol_src+num_max; i++, j++)
                head[i]=String.format("i%d", j);
            //

            int n = -1;
            for (Element el : cir)
                switch (el.get_type()) {
                    case "R":
                        A.setElement(el.get_nump(), el.get_nump(), A.getElement(el.get_nump(), el.get_nump())
                                + (1.0 / el.get_val()));
                        A.setElement(el.get_numm(), el.get_numm(), A.getElement(el.get_numm(), el.get_numm())
                                + (1.0 / el.get_val()));
                        A.setElement(el.get_nump(), el.get_numm(), A.getElement(el.get_nump(), el.get_numm())
                                - (1.0 / el.get_val()));
                        A.setElement(el.get_numm(), el.get_nump(), A.getElement(el.get_numm(), el.get_nump())
                                - (1.0 / el.get_val()));
                        break;
                    case "I":
                        z.setElement(el.get_nump(), 0, z.getElement(el.get_nump(), 0) - el.get_val());
                        z.setElement(el.get_numm(), 0, z.getElement(el.get_numm(), 0) + el.get_val());
                        break;
                    case "V":
                        for (i = 0; i < tmp; i++)
                            if (head[i].equals(String.format("i%d", el.get_num()))) {
                                n = i+1;
                                break;
                            }
                        A.setElement(n, el.get_nump(), A.getElement(n, el.get_nump()) + 1.0);
                        A.setElement(n, el.get_numm(), A.getElement(n, el.get_numm()) - 1.0);
                        A.setElement(el.get_nump(), n, A.getElement(el.get_nump(), n) + 1.0);
                        A.setElement(el.get_numm(), n, A.getElement(el.get_numm(), n) - 1.0);
                        z.setElement(n, 0, el.get_val());
                        break;
                    default:
                        break;
                }
            tmp--;
            double[][] new_A = new double[tmp][tmp], new_z = new double[tmp][1];
            tmp++;
            for(i = 1; i < tmp; i++) {
                new_z[i-1][0]=z.getElement(i,0);

                for (j = 1; j < tmp; j++)
                    new_A[i - 1][j - 1] = A.getElement(i, j);
            }
            z = new Matrix(new_z);
            A = new Matrix(new_A);
            Matrix x = A.solve(z);
            StringBuilder sb = new StringBuilder();
            for(i = 0; i < tmp-1; i++)
            {
                switch (head[i].substring(0,1)){
                    case "v":
                        sb.append(String.format("The voltage at node %s: %.4f%n", head[i].substring(1), x.getElement(i, 0)));
                        break;
                    case "i":
                        sb.append(String.format("The current through %s: %.4f%n", head[i], x.getElement(i, 0)));
                        break;
                }
            }
            System.out.printf("Matrix A:%n%s%nMatrix z:%n%s%nMatrix x:%n%s%n%s%n", A.toString(), z.toString(), x.toString(), sb.toString());
        }
    }

    public static List<Element> parseFile(String filename)
    {
        File file = new File(filename);
        List<Element> cir = new ArrayList<Element>();
        String content;
        int line = 0;
        try {
            FileReader reader = new FileReader(file);
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            content = new String(chars);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.printf("Read %s file error\n", filename);
            return null;
        }
        try {
            String[] ls = content.split("\n");
            String[] tmp;
            for (String l : ls) {
                tmp = l.split(" ");
                if (l.charAt(0) == '*')
                    continue;
                if (tmp[0].startsWith("D"))
                    cir.add(new Element(tmp[0].substring(0, 1), Integer.parseInt(tmp[0].substring(1)), Integer.parseInt(tmp[1]), Integer.parseInt(tmp[2]), 0.0));
                else
                    cir.add(new Element(tmp[0].substring(0, 1), Integer.parseInt(tmp[0].substring(1)), Integer.parseInt(tmp[1]), Integer.parseInt(tmp[2]), Double.parseDouble(tmp[3])));
                line++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.printf("Parse %s file error at %d line\n", filename, line);
        }
        return cir;
    }
}

