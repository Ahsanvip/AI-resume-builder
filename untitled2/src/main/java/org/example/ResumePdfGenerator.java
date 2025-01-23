import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.DashedLine;
import com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.properties.TextAlignment;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.Map;

public class ResumePdfGenerator {
    public static void main(String[] args) {
        String jsonFilePath = "src/main/resources/rresume_data.json"; // Path to your JSON file
        String pdfFilePath = "beautiful_resume.pdf";

        try {
            // Parse JSON
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> jsonMap = objectMapper.readValue(new File(jsonFilePath), Map.class);

            // Extract fields
            String name = (String) jsonMap.getOrDefault("name", "No name provided.");
            String email = (String) jsonMap.getOrDefault("email", "No email provided.");
            String phone = (String) jsonMap.getOrDefault("phone", "No phone provided.");
            String linkedIn = (String) jsonMap.getOrDefault("linkedin", "https://www.linkedin.com");
            String github = (String) jsonMap.getOrDefault("github", "https://github.com");

            String summary = (String) jsonMap.getOrDefault("summary_section", "No summary provided.");
            String experience = (String) jsonMap.getOrDefault("experience_section", "No experience provided.");
            String skills = (String) jsonMap.getOrDefault("skills_section", "No skills provided.");
            String education = (String) jsonMap.getOrDefault("education_section", "No education provided.");

            // Create PDF
            PdfWriter writer = new PdfWriter(pdfFilePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Profile Section (Dynamic Cells)
            Table profileTable = new Table(new float[]{3, 6, 3, 3, 3}).useAllAvailableWidth(); // Set relative widths
            profileTable.setMarginBottom(10);

            // Add profile data to cells
            profileTable.addCell(createCell(name, TextAlignment.LEFT,ColorConstants.DARK_GRAY));
            profileTable.addCell(createCell(email, TextAlignment.LEFT,ColorConstants.DARK_GRAY));
            profileTable.addCell(createCell(phone, TextAlignment.LEFT,ColorConstants.DARK_GRAY));

            // Add LinkedIn with a clickable link
            Link linkedInLink = new Link("LinkedIn", com.itextpdf.kernel.pdf.action.PdfAction.createURI(linkedIn));
            profileTable.addCell(createCell(new Paragraph().add(linkedInLink), TextAlignment.LEFT,ColorConstants.DARK_GRAY));

            // Add GitHub with a clickable link
            Link githubLink = new Link("GitHub", com.itextpdf.kernel.pdf.action.PdfAction.createURI(github));
            profileTable.addCell(createCell(new Paragraph().add(githubLink), TextAlignment.LEFT,ColorConstants.DARK_GRAY));

            // Add the formatted profile table to the document
            document.add(profileTable);

            // Skills Section
            addSectionWithBackground(document, "Skills", ColorConstants.LIGHT_GRAY);
            document.add(new Paragraph(skills).setFontSize(12));
            addLineSeparator(document, new DashedLine());

            // Experience Section
            addSectionWithBackground(document, "Experience", ColorConstants.LIGHT_GRAY);
            document.add(new Paragraph(experience).setFontSize(12));
            addLineSeparator(document, new SolidLine());

            // Summary Section
            addSectionWithBackground(document, "Summary", ColorConstants.LIGHT_GRAY);
            document.add(new Paragraph(summary).setFontSize(12));
            addLineSeparator(document, new SolidLine());

            // Education Section
            addSectionWithBackground(document, "Education", ColorConstants.LIGHT_GRAY);
            document.add(new Paragraph(education).setFontSize(12));
            addLineSeparator(document, new DashedLine());

            // Close the document
            document.close();
            System.out.println("PDF generated successfully at: " + pdfFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred while generating the PDF.");
        }
    }

    // Helper method to create a dynamic cell
    private static Cell createCell(String content, TextAlignment alignment, com.itextpdf.kernel.colors.Color backgroundColor) {
        return new Cell()
                .add(new Paragraph(content))
                .setBackgroundColor(backgroundColor) // Set background color here
                .setTextAlignment(alignment)
                .setBorder(Border.NO_BORDER)
                .setFontColor(ColorConstants.WHITE)
                .setPadding(5); // Add padding to avoid text overflow
    }

    private static Cell createCell(Paragraph content, TextAlignment alignment, com.itextpdf.kernel.colors.Color backgroundColor) {
        return new Cell()
                .add(content)
                .setTextAlignment(alignment)
                .setFontColor(ColorConstants.WHITE)
                .setBackgroundColor(backgroundColor) // Set background color here
                .setBorder(Border.NO_BORDER)
                .setPadding(5); // Add padding to avoid text overflow
    }

    // Helper method to add a line separator
    private static void addLineSeparator(Document document, ILineDrawer lineDrawer) {
        LineSeparator separator = new LineSeparator(lineDrawer);
        separator.setWidth(UnitValue.createPercentValue(100));
        separator.setMarginTop(10);
        separator.setMarginBottom(10);
        document.add(separator);
    }

    private static void addSectionWithBackground(Document document, String headingText, com.itextpdf.kernel.colors.Color backgroundColor) {
        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        Cell cell = new Cell().add(new Paragraph(headingText).setFontSize(16).setBold().setFontColor(ColorConstants.WHITE));
        cell.setBackgroundColor(backgroundColor);
        cell.setBorder(Border.NO_BORDER);
        cell.setPadding(5);
        table.addCell(cell);
        document.add(table);
    }
}
