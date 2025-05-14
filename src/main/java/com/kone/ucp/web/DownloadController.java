package com.kone.ucp.web;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.kone.ucp.dto.ContractInfoDto;
import com.kone.ucp.dto.ExcelRequestDTO;
import com.kone.ucp.dto.MemberSecurityDto;
import com.kone.ucp.service.ContractService;
import com.kone.ucp.service.ImageService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.font.FontProvider;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/download")
@Controller
public class DownloadController {
	private final SpringTemplateEngine templateEngine;
	private final ContractService conSvc;
	private final ImageService iUtil;

	@PreAuthorize("hasAnyRole('USER','BRANCH')")
	@GetMapping("/pdf")
	public void downloadPdf(HttpServletResponse response, @AuthenticationPrincipal MemberSecurityDto msd, Model model) {
		log.info("GET - downloadPdf");

		try {
			ContractInfoDto m = conSvc.getMemberInfoCheck(msd.getId());

			// PDF 파일 이름 지정
			String fileName = "contract.pdf";
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

			// HTML 데이터 생성
			model.addAttribute("info", m);

			// 타임리프 템플릿에서 html 렌더링
			Context context = new Context();
			context.setVariables(model.asMap());
			String htmlContent = templateEngine.process("layout/pdf_sample", context);

			// 한글 폰트 설정
			FontProgram fontProgram = FontProgramFactory
					.createFont(new ClassPathResource("C:/Windows/Fonts/malgun.ttf").getPath());
			FontProvider fontProvider = new FontProvider();
			fontProvider.addFont(fontProgram);

			ConverterProperties converterProperties = new ConverterProperties();
			converterProperties.setFontProvider(fontProvider);

			// HTML을 PDF로 변환
			OutputStream out = response.getOutputStream();
			PdfWriter writer = new PdfWriter(out);
			PdfDocument pdf = new PdfDocument(writer);
			HtmlConverter.convertToPdf(htmlContent, pdf, converterProperties);

			out.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("/excel")
	public void downloadExcel(HttpServletResponse response) throws IOException {
		List<ExcelRequestDTO> es = conSvc.getExcelInfo();

		Workbook workbook = new HSSFWorkbook(); // 엑셀파일 생성
		Sheet sheet = workbook.createSheet("sheet1"); // 시트 생성
		int rowNo = 0;

		Row headerRow = sheet.createRow(rowNo++);
		headerRow.createCell(0).setCellValue("이름");
		headerRow.createCell(1).setCellValue("연락처");
		headerRow.createCell(2).setCellValue("근무일자");
		headerRow.createCell(3).setCellValue("근무일수");
		headerRow.createCell(4).setCellValue("근무지역");
		headerRow.createCell(5).setCellValue("근무학교");
		headerRow.createCell(6).setCellValue("금액");
		headerRow.createCell(7).setCellValue("주민번호");
		headerRow.createCell(8).setCellValue("계좌번호");
		headerRow.createCell(9).setCellValue("은행명");
		headerRow.createCell(10).setCellValue("예금주");
		headerRow.createCell(11).setCellValue("비고");
		
        CellStyle phonenumberStyle = workbook.createCellStyle();
        DataFormat phonenumberFormat = workbook.createDataFormat();
        phonenumberStyle.setDataFormat(phonenumberFormat.getFormat("000-0000-0000"));
        
        // 주민번호 스타일 생성
        CellStyle resinumberStyle = workbook.createCellStyle();
        DataFormat resinumberFormat = workbook.createDataFormat();
        resinumberStyle.setDataFormat(resinumberFormat.getFormat("000000-0000000"));
        
        // 비용 스타일 생성
        CellStyle payStyle = workbook.createCellStyle();
        DataFormat payFormat = workbook.createDataFormat();
        payStyle.setDataFormat(payFormat.getFormat("#,##0"));
        
        
		for (ExcelRequestDTO e : es) {
			Row row = sheet.createRow(rowNo++);
			row.createCell(0).setCellValue(e.getName());
			
			row.createCell(1).setCellValue(Double.parseDouble(e.getPhoneNumber()));
			row.getCell(1).setCellStyle(phonenumberStyle);
			
			row.createCell(2).setCellValue(e.getTestDate().getMonthValue()+"/"+e.getTestDate().getDayOfMonth());
			
			row.createCell(3).setCellValue(e.getOneday());
			row.createCell(4).setCellValue(e.getArea());
			row.createCell(5).setCellValue(e.getSchoolName());
			
			row.createCell(6).setCellValue(e.getPay());
			row.getCell(6).setCellStyle(payStyle);
			
			row.createCell(7).setCellValue(Double.parseDouble(e.getResidentNumber()));
			row.getCell(7).setCellStyle(resinumberStyle);
			
			row.createCell(8).setCellValue(e.getAccountNumber());
			row.createCell(9).setCellValue(e.getBank());
			row.createCell(10).setCellValue(e.getAccountHolder());
		}
		
		for(int i = 0; i < 11; i++) {
			sheet.autoSizeColumn(i);
			sheet.setColumnWidth(i, (sheet.getColumnWidth(i))+1024); //너비 더 넓게
		}
		
		response.setContentType("ms-vnd/excel");
		response.setHeader("Content-Disposition", "attachment;filename=list.xls");

		workbook.write(response.getOutputStream());
		workbook.close();
	}
	
	// 이미지 다운로드 링크 만들기
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@GetMapping("/accountImg/{id}")
	public ResponseEntity<Resource> downAccountImg(@PathVariable("id")Long id) {
		try {
			return iUtil.downOneImg(id, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// 이미지 다운로드 링크 만들기
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@GetMapping("/idCardImg/{id}")
	public ResponseEntity<Resource> downIdCardImg(@PathVariable("id")Long id) {
		try {
			return iUtil.downOneImg(id, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
