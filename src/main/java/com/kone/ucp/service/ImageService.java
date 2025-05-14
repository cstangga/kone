package com.kone.ucp.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import com.kone.ucp.domain.Image;
import com.kone.ucp.domain.Member;
import com.kone.ucp.repo.ImageRepo;
import com.kone.ucp.repo.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImageService {

	private final ImageRepo imageDao;
	private final MemberRepository memberDao;

	private static final String pathName = "C:\\uploads\\";

	/**
	 * 이미지파일을 c드라이브에 저장하며, 파일경로를 db에 저장
	 * 
	 * @param id    : 멤버 아이디
	 * @param image : 이미지 타입의 배열
	 * 
	 * @return : 이미지 저장 성공 시 true, 실패 시 false 반환
	 */
	// 이미지 타입별 저장
	public boolean saveImg(Long id, MultipartFile accountFile, MultipartFile cardFile) {
		try {
			String accountName = UUID.randomUUID().toString().replace("-", "") + "_"
					+ accountFile.getOriginalFilename();
			String accountAllPathUrl = pathName + accountName;
			accountFile.transferTo(new File(accountAllPathUrl));

			String cardName = UUID.randomUUID().toString().replace("-", "") + "_" + cardFile.getOriginalFilename();
			String cardAllPathUrl = pathName + cardName;
			cardFile.transferTo(new File(cardAllPathUrl));

			Member m = memberDao.findById(id).orElse(null);

			Image img = Image.builder().member(m).accoutImg(accountName).idCardImg(cardName).build();
			imageDao.save(img);

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Transactional
	public void updateImg(Long mId, MultipartFile file, int idx) {
		try {
			String imgUrlName = UUID.randomUUID().toString().replace("-", "") + "_" + file.getOriginalFilename();
			String allPathUrl = pathName + imgUrlName;

			file.transferTo(new File(allPathUrl));

			Member m = memberDao.findById(mId).orElse(null);
			Image i = imageDao.findByMember(m);

			if (idx == 0) {
				String imgName = i.getAccoutImg();
				File deleteFile = new File("/uploads/" + imgName);
				deleteFile.delete();

				i.updateAccoutImg(imgUrlName);
			}

			if (idx == 1) {
				String imgName = i.getIdCardImg();
				File deleteFile = new File("/uploads/" + imgName);
				deleteFile.delete();

				i.updateIdCardImg(imgUrlName);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Image getAllImg(Member m) {
		return imageDao.findByMember(m);
	}

	/**
	 * 멤버 아이디로 검색하여 이미지 전체 삭제
	 * 
	 * @param id : 멤버 아이디
	 * @return : 이미지가 정상적으로 삭제되었으면 true, 정상적으로 삭제되지 않았다면 false 반환
	 */
	@Transactional
	public void delAllImg(Long id) {
		log.info("@@@ImageService : delAllImg() 실행");
		Member m = memberDao.findById(id).orElse(null);

		Image imgs = imageDao.findByMember(m);

		List<String> names = imgs.getAllUrlName();

		for (String name : names) {
			File file = new File("/uploads/" + name);
			log.info("@@@이미지 삭제 : " + file + " =  " + file.delete());
		}

		imageDao.deleteByMember(m);
	}

	/**
	 * 이미지 해당 아이디로 검색하여 이미지 다운로드함
	 * 
	 * @param id : 이미지 해당 아이디
	 * @return : ResponseEntity<Resource> 타입을 반환함으로 이 함수 사용시, Controller에서 return
	 *         Service.fileDown(id) 이대로 사용해도 무방
	 * @throws IOException
	 */
	public ResponseEntity<Resource> downOneImg(Long id, int idx) throws IOException {

		Image i = imageDao.findById(id).orElse(null);
		String fileName = null;
		UrlResource resource = null;
		String encodedOriginalFileName = null;
		String contentDisposition = null;

		if (idx == 0) {
			fileName = i.getAccoutImg();

			resource = new UrlResource("file:" + pathName + fileName);

		} else {
			fileName = i.getIdCardImg();

			resource = new UrlResource("file:" + pathName + fileName);

		}
		encodedOriginalFileName = UriUtils.encode(fileName, StandardCharsets.UTF_8);
		contentDisposition = "attachment; filename=\"" + encodedOriginalFileName + "\"";

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition).body(resource);
	}
}
