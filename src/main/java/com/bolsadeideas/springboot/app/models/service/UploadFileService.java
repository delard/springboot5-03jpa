package com.bolsadeideas.springboot.app.models.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;


@Service
public class UploadFileService implements IUploadFileService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final static String UPLOADS_FOLDER = "uploads";

	@Override
	public Resource load(String filename) throws MalformedURLException {

		Path pathFoto = getPath(filename);
		log.info("pathFoto: " + pathFoto);

		Resource recurso = new UrlResource(pathFoto.toUri());
		if (!recurso.exists() && !recurso.isReadable()) {
			throw new RuntimeException("La imagen '" + pathFoto.toUri() + "' no existe o no tiene permiso de lectura");
		}
		return recurso;
	}

	@Override
	public String copy(MultipartFile file) throws IOException {

		String uniqueFilenameString = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
		Path rootPath = getPath(uniqueFilenameString);
		log.info("rootPath: " + rootPath);
		Files.copy(file.getInputStream(), rootPath);
		return uniqueFilenameString;
	}

	@Override
	public boolean delete(String filename) {

		Path rootPath;
		rootPath = getPath(filename).toAbsolutePath();
		File archivoFiles = rootPath.toFile();
		if (archivoFiles.exists() && archivoFiles.canWrite()) {
			if (archivoFiles.delete()) {
				return true;
			}
		}
		return false;

	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(Paths.get(UPLOADS_FOLDER).toFile());
	}

	@Override
	public void init() throws IOException {
		Files.createDirectory(Paths.get(UPLOADS_FOLDER));
	}

	public Path getPath(String filename) {
		return Paths.get(UPLOADS_FOLDER).resolve(filename).toAbsolutePath();
	}

}
