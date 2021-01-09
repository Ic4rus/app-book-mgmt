import { useEffect, useState } from 'react';
import {
  Button,
  Card,
  Col,
  Pagination,
  Progress,
  Spin,
  Typography,
  Upload,
} from 'antd';
import Search from 'antd/lib/input/Search';
import { PlusOutlined, CloseOutlined } from '@ant-design/icons';

import * as action from './Action';

import 'antd/dist/antd.css';
import './App.scss';
import { Document, Page, pdfjs } from 'react-pdf';
import Layout, { Content } from 'antd/lib/layout/layout';
import Sider from 'antd/lib/layout/Sider';
import Lottie from 'react-lottie';
import animationData from './18077-book-read.json';

pdfjs.GlobalWorkerOptions.workerSrc = `//cdnjs.cloudflare.com/ajax/libs/pdf.js/${pdfjs.version}/pdf.worker.js`;

const { Meta } = Card;

export const Book = (props) => {
  const { isBook } = props;

  return isBook ? (
    <Col span={6}>
      <Card
        hoverable
        style={{ width: 160 }}
        cover={
          <img
            alt='example'
            src='https://os.alipayobjects.com/rmsportal/QBnOOoLaAfKPirc.png'
          />
        }
      >
        <Meta title='Europe Street beat' description='www.instagram.com' />
      </Card>
    </Col>
  ) : (
    <Col span={6} style={{ minHeight: '270px' }}>
      <Upload
        name='file'
        listType='picture-card'
        className='uploader'
        showUploadList={false}
        action='http://localhost:8080/books/upload'
      >
        <PlusOutlined />
      </Upload>
    </Col>
  );
};

const App = () => {
  const [keyword, setKeyword] = useState(undefined);
  const [fileList, setFileList] = useState([]);
  const [file, setFile] = useState({});

  useEffect(() => {
    loadBooks();
  }, []);

  const loadBooks = async (keyword) => {
    const { data } = await action.fetchAllBooks(keyword);

    if (data.code === 0) {
      const fileList = data.data;

      fileList.forEach((value, index) => (value.uid = index));

      setKeyword(keyword);
      setFileList(fileList);
    }
  };

  const handleChange = ({ file, fileList }) => {
    if (file.status === 'done') {
      const { response } = file;

      const book = response.data;

      fileList
        .filter((value) => value.uid === file.uid)
        .forEach((value) => {
          value.title = book.title;
          value.author = book.author;
          value.filePath = book.filePath;
          value.thumbnailPath = book.thumbnailPath;
          value.numberOfPages = book.numberOfPages;
          value.pageNumber = 1;
        });
    }

    setFileList(fileList);
  };

  const itemRender = (_originNode, file, _fileList) => {
    const { status, percent } = file;

    if (status === 'uploading') {
      return (
        <div className='uploading'>
          <Progress percent={percent} />
          <span className='file-name'>{file.name}</span>
        </div>
      );
    } else {
      return (
        <Card
          key={file.filePath}
          hoverable
          cover={
            keyword ? (
              <span
                className='highlight'
                dangerouslySetInnerHTML={{
                  __html: file.highlight ? file.highlight.text : '',
                }}
              ></span>
            ) : (
              <img
                alt=''
                src={`http://localhost:8080/books/download?filePath=${file.thumbnailPath}&isBook=false`}
                style={{ width: 160, height: 200, backgroundSize: 'cover' }}
              />
            )
          }
          onClick={() => handleShowContent(file)}
        >
          <Meta
            title={file.title}
            description={
              keyword
                ? 'Page ' + (file.highlight ? file.highlight.pageNumber : 'N/A')
                : file.author
                ? file.author
                : 'N/A'
            }
          />
        </Card>
      );
    }
  };

  const handleShowContent = (selectedFile) => {
    console.log('Show content', selectedFile);

    if (selectedFile.filePath !== file.filePath) {
      selectedFile.loading = true;
    } else {
      selectedFile.loading = false;
    }

    selectedFile.pageNumber = selectedFile.highlight
      ? selectedFile.highlight.pageNumber
      : 1;

    setFile(selectedFile);
  };

  const handleLoadSuccess = () => {
    console.log('Load success');

    file.loading = false;

    setFile({ ...file });
  };

  const handleChangePage = (page) => {
    file.pageNumber = page;

    setFile({ ...file });
  };

  const handleSearch = (keyword) => {
    loadBooks(keyword.trim());
  };

  const lottieOptions = {
    loop: true,
    autoplay: true,
    animationData: animationData,
    rendererSettings: {
      preserveAspectRatio: 'xMidYMid slice',
    },
  };

  return (
    <Layout>
      <Layout>
        <Content>
          <div className='app-container'>
            <Lottie options={lottieOptions} height={100} width={100} />
            <Search
              placeholder='Keyword'
              enterButton
              size='large'
              className='search-bar'
              onSearch={handleSearch}
            />
            <div className='book-list'>
              <Upload
                action='http://localhost:8080/books/upload'
                listType='picture-card'
                fileList={fileList}
                itemRender={itemRender}
                onChange={handleChange}
              >
                {!keyword && (
                  <div>
                    <PlusOutlined />
                    <div style={{ marginTop: 8 }}>Upload</div>
                  </div>
                )}
              </Upload>
            </div>
          </div>
        </Content>
      </Layout>
      <Sider width={file.title ? 616 : 0}>
        {file.title && (
          <div className='sider'>
            <div className='menu-bar'>
              <Button
                type='text'
                style={{ color: '#fff' }}
                size='large'
                icon={<CloseOutlined />}
                onClick={() => setFile({})}
              />
            </div>
            <div className='content'>
              <Spin spinning={file.loading}>
                <Document
                  file={`http://localhost:8080/books/download?filePath=${file.filePath}`}
                  onLoadSuccess={handleLoadSuccess}
                >
                  <Page pageNumber={file.pageNumber} />
                </Document>
              </Spin>
            </div>
            <Pagination
              current={file.pageNumber}
              pageSize={1}
              total={file.numberOfPages}
              onChange={handleChangePage}
              simple
            />
          </div>
        )}
      </Sider>
    </Layout>
  );
};

export default App;
