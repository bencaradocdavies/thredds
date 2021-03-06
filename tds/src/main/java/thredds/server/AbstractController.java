/*
 * Copyright 1998-2014 University Corporation for Atmospheric Research/Unidata
 *
 *   Portions of this software were developed by the Unidata Program at the
 *   University Corporation for Atmospheric Research.
 *
 *   Access and use of this software shall impose the following obligations
 *   and understandings on the user. The user is granted the right, without
 *   any fee or cost, to use, copy, modify, alter, enhance and distribute
 *   this software, and any derivative works thereof, and its supporting
 *   documentation for any purpose whatsoever, provided that this entire
 *   notice appears in all copies of the software, derivative works and
 *   supporting documentation.  Further, UCAR requests that the user credit
 *   UCAR/Unidata in any publications that result from the use of this
 *   software or in any product that includes this software. The names UCAR
 *   and/or Unidata, however, may not be used in any advertising or publicity
 *   to endorse or promote any products or commercial entity unless specific
 *   written permission is obtained from UCAR/Unidata. The user also
 *   understands that UCAR/Unidata is not obligated to provide the user with
 *   any support, consulting, training or assistance of any kind with regard
 *   to the use, operation and performance of this software nor to provide
 *   the user with any updates, revisions, new versions or "bug fixes."
 *
 *   THIS SOFTWARE IS PROVIDED BY UCAR/UNIDATA "AS IS" AND ANY EXPRESS OR
 *   IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *   WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *   DISCLAIMED. IN NO EVENT SHALL UCAR/UNIDATA BE LIABLE FOR ANY SPECIAL,
 *   INDIRECT OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING
 *   FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT,
 *   NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION
 *   WITH THE ACCESS, USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package thredds.server;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import thredds.servlet.ServletUtil;
import thredds.util.TdsPathUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;

/**
 * For Annotated Spring Controllers, going through the root servlet
 *
 * @author caron
 * @since 10/20/13
 */
public abstract class AbstractController {

  abstract protected String getControllerPath();
  abstract protected String[] getEndings();

  public String getDatasetPath(HttpServletRequest req) {
    String path = TdsPathUtils.extractPath(req, getControllerPath());

    // strip off endings
    for (String ending : getEndings()) {
      if (path.endsWith(ending)) {
        int len = path.length() - ending.length();
        path = path.substring(0, len);
        break;
      }
    }

    return path;
  }

  public String getAbsolutePath(HttpServletRequest req) {
     return ServletUtil.getRequestServer(req) + req.getContextPath() + req.getServletPath();
  }

  @ExceptionHandler(FileNotFoundException.class)
 	public ResponseEntity<String> handle(FileNotFoundException ncsse) {
 		HttpHeaders responseHeaders = new HttpHeaders();
 		responseHeaders.setContentType(MediaType.TEXT_PLAIN);
 		return new ResponseEntity<>(
 				"FileNotFoundException exception handled : " + ncsse.getMessage(), responseHeaders,
 				HttpStatus.NOT_FOUND);
 	}

 	@ExceptionHandler(UnsupportedOperationException.class)
 	public ResponseEntity<String> handle(UnsupportedOperationException ex) {
 		HttpHeaders responseHeaders = new HttpHeaders();
 		responseHeaders.setContentType(MediaType.TEXT_PLAIN);
 		return new ResponseEntity<>(
 				"UnsupportedOperationException exception handled : " + ex.getMessage(), responseHeaders,
 				HttpStatus.BAD_REQUEST);
 	}

  @ExceptionHandler(Throwable.class)
 	public ResponseEntity<String> handle(Throwable ex) {
   //  ex.printStackTrace();

    org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());
    logger.error("uncaught exception", ex);

 		HttpHeaders responseHeaders = new HttpHeaders();
 		responseHeaders.setContentType(MediaType.TEXT_PLAIN);
 		return new ResponseEntity<>("Throwable exception handled : " + ex.getMessage(), responseHeaders,
             HttpStatus.INTERNAL_SERVER_ERROR);
 	}

}
