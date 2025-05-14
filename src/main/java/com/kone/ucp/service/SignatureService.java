package com.kone.ucp.service;

import org.springframework.stereotype.Service;

import com.kone.ucp.domain.Image;
import com.kone.ucp.domain.Member;
import com.kone.ucp.repo.ImageRepo;
import com.kone.ucp.repo.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.UUID;
import java.io.IOException;
import java.io.File;

@Slf4j
@RequiredArgsConstructor
@Service
public class SignatureService {
	private final MemberRepository memberDao;
	private final ImageRepo imgDao;

	// 저장 경로를 Windows 스타일로 변경
	private static final String SIGNATURE_PATH = "C:\\uploads\\";

	// Base64 데이터를 파일로 저장하는 메서드
	public String saveSignatureImage(String base64Data, String fileName) throws IOException {
		// Base64 디코딩
		String[] parts = base64Data.split(",");
		byte[] decodedBytes = Base64.getDecoder().decode(parts[1]);

		String imgUrlName = UUID.randomUUID().toString().replace("-", "") + "_" + fileName;

		// 파일 경로 설정
		File file = new File(SIGNATURE_PATH + imgUrlName);
		java.nio.file.Files.write(file.toPath(), decodedBytes);

		return file.getName();
	}

	@Transactional
	public String updateSignatureImage(Long mId, String base64Data, String fileName, int idx) throws IOException {

		Member m = memberDao.findById(mId).orElse(null);
		Image i = imgDao.findByMember(m);
		
		//파일 삭제
		if(idx == 2) {
			String imgName = i.getSign1Img();
			File deleteFile = new File("/uploads/"+imgName);
			deleteFile.delete();
		}else {
			String imgName = i.getSign2Img();
			File deleteFile = new File("/uploads/"+imgName);
			deleteFile.delete();
		}

		// Base64 디코딩
		String[] parts = base64Data.split(",");
		
		byte[] decodedBytes = Base64.getDecoder().decode(parts[1]);

		String imgUrlName = UUID.randomUUID().toString().replace("-", "") + "_" + fileName;

		// 파일 경로 설정
		File file = new File(SIGNATURE_PATH + imgUrlName);
		java.nio.file.Files.write(file.toPath(), decodedBytes);

		return file.getName();
	}

}
