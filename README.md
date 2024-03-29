
# CSV Processing Spring Boot Application

This application provides an API for uploading trade.csv files, enriching them with product names from 
a static product.csv file, and returning the enriched data. It's designed to handle large files efficiently 
and can process multiple requests concurrently.

### Features:

- File Upload: Accepts trade.csv files through a REST API endpoint.
- Data Enrichment: Enriches trade data by replacing product_id with corresponding product_name.
- Concurrency Support: Capable of handling multiple file upload requests concurrently.
- Validation: Validates the date format in trade.csv files and logs errors for invalid formats or missing product names.

### API Reference

#### Upload and Enrich Trade Data:
- URL: /api/trades/enrich
- Method: POST
- Content-Type: multipart/form-data
- Form Data:
  - Key: file
- Value: The trade.csv file to be uploaded
- Success Response:
  - Code: 200 OK
  - Content: Returns enriched trade data in CSV format.
- Error Response:
  - Code: 400 BAD REQUEST (If the file is not provided or is invalid)
  - Code: 500 INTERNAL SERVER ERROR (For processing errors)

  #### cURL Example:

```
curl --location 'http://localhost:8080/api/v1/enrich' \
--form 'file=@" absolutePathTo -> trade.csv"'
```
### Weak Sides
- File-Based Data Source: Relying on a static product.csv for product names might limit scalability and real-time 
data accuracy. For dynamic and growing datasets, integrating a database or an external API could offer more 
flexibility and up-to-date information.
- Error Handling: While basic validation and logging are implemented, more sophisticated error handling and user 
feedback mechanisms might be necessary, especially for identifying and correcting data issues in uploaded files.
- Single File Format Support: The application is tailored to CSV files, which might limit its applicability. 
Extending support to other file formats or data sources could enhance its versatility.
- Performance Under Extreme Load: While efficient for large files, the application's performance under extreme 
loads or with exceptionally large datasets hasn't been explicitly addressed. Performance optimizations and stress 
testing might be necessary for high-demand environments.
- Security Considerations: Uploading files to a server inherently introduces security 
risks (e.g., malicious file uploads). The application's current description doesn't detail specific 
security measures beyond Spring Boot's defaults.
- Manual Setup for product.csv: The need to manually place the product.csv in a specific directory prior to 
runtime could complicate deployment and updates, especially in containerized or cloud environments. 
Automating data updates or sourcing product information from a managed 
service could improve usability.

## What could be changed?
If I had more time to further develop and refine this application, 
I would consider incorporating several enhancements to improve its 
efficiency, scalability, and robustness. 
Here are some key improvements I would aim to implement:
### Integration with an H2 Database
- Persistent Data Storage: Utilizing an H2 database would allow for more 
dynamic and scalable management of product information. 
Instead of relying on a static product.csv, product data could be stored 
in a database, making it easier to update, query, and manage large datasets.
- Streamlined Data Access: With H2, I could implement more sophisticated 
querying and data retrieval mechanisms. This would facilitate 
efficient lookups of product names based on product IDs directly 
from the database, enhancing the application's performance, especially 
with large datasets.
### Stream Processing of Records
- Efficient Data Handling: To optimize memory usage and handle very large 
CSV files more effectively, I would implement a stream-based approach 
for processing records. This would involve reading and processing 
each record from the trade.csv file in a stream, without loading the 
entire file into memory at once.
- Real-time Data Processing: Stream processing would enable the application 
- to start processing data as soon as it is received, reducing the overall 
latency from file upload to data enrichment.
### Use of ThreadPoolTaskExecutor for Asynchronous Processing
- Concurrent Processing: By leveraging Spring's ThreadPoolTaskExecutor, 
I could process multiple trade files concurrently, significantly improving 
the application's throughput. This would be particularly beneficial for 
handling multiple simultaneous upload requests in a more efficient manner.
- Resource Management: A task executor would allow for fine-grained control 
over the number of concurrent threads, helping to manage the application's 
resource utilization and ensuring that the system remains responsive under 
load.
### Transactional Data Operations
- Data Integrity: In scenarios involving updates to the database 
(e.g., adding or modifying product data), I would use Spring's 
@Transactional annotation to ensure that data operations are executed 
within a transactional context. This would safeguard against partial 
updates and maintain the integrity of the data in the event of errors 
or failures.