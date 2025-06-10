package com.study.authenticationservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Service {

    /**
     * Tải lên một tệp vào bucket Amazon S3.
     *
     * Phương thức này chấp nhận một `MultipartFile` và tải lên nó vào bucket S3 đã được cấu hình.
     * Tệp sẽ được lưu trữ với tên tệp gốc của nó làm khóa trong S3. Sau khi tải lên thành công,
     * phương thức sẽ trả về URL của tệp đã được tải lên.
     *
     * @param file tệp cần được tải lên Amazon S3.
     * @return URL của tệp đã được tải lên trong S3.
     * @throws Exception nếu có bất kỳ lỗi nào xảy ra trong quá trình tải lên.
     * @throws IOException nếu có vấn đề với I/O tệp trong quá trình tải lên.
     */
    String uploadFile(MultipartFile file) throws Exception, IOException;

    /**
     * Xóa một tệp từ bucket Amazon S3.
     *
     * Phương thức này chấp nhận URL của một tệp và xóa nó khỏi bucket S3.
     * Nó trích xuất khóa của tệp từ URL đã cung cấp và sử dụng nó để xác định
     * và loại bỏ tệp khỏi S3.
     *
     * @param url URL của tệp cần được xóa khỏi S3.
     * @return một thông báo xác nhận cho biết tệp đã được xóa thành công hay chưa.
     * @throws Exception nếu có bất kỳ lỗi nào xảy ra trong quá trình xóa tệp.
     */
    void deleteFile(String url) throws Exception;

    /**
     * Tải xuống một tệp từ Amazon S3.
     *
     * @param url URL của tệp cần được tải xuống từ S3.
     * @return nội dung tệp dưới dạng một chuỗi hoặc đường dẫn tệp cục bộ nơi tệp được lưu.
     * @throws Exception nếu có lỗi xảy ra trong quá trình tải xuống.
     */
    String downloadFile(String url) throws Exception;

}
