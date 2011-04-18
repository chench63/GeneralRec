
// CrazySpotHelperDlg.cpp : implementation file
//

#include "stdafx.h"
#include <fstream>
#include "CrazySpotHelper.h"
#include "CrazySpotHelperDlg.h"
#include "TranDlg.h"
#include "afxdialogex.h"
#include <time.h>
#include <vector>



	#define NO_DEBUG_1
	#define NO_DEBUG_2

#ifdef NO_DEBUG
	#define NO_DEBUG_1
	#define NO_DEBUG_2
	#define NO_DEBUG_3
#endif



#ifndef NO_DEBUG_3
	std::vector<clock_t> start,finish;
	#define STARTTIME() {start.push_back(clock());}
	#define ENDTIME() {finish.push_back(clock());}
#endif
#define nPicWidth  497  
#define nPicHeight  448
#define nOffsetLeftPicX  8
#define nOffsetLeftPicY  193
#define nOffsetRightPicX  517
#define nOffsetRightPicY  193


// CCrazySpotHelperDlg dialog




CCrazySpotHelperDlg::CCrazySpotHelperDlg(CWnd* pParent /*=NULL*/)
	: CDialogEx(CCrazySpotHelperDlg::IDD, pParent)
{
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
}

void CCrazySpotHelperDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
}

BEGIN_MESSAGE_MAP(CCrazySpotHelperDlg, CDialogEx)
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	ON_BN_CLICKED(IDCANCEL, &CCrazySpotHelperDlg::OnBnClickedCancel)
	ON_BN_CLICKED(IDOK, &CCrazySpotHelperDlg::OnBnClickedOk)
END_MESSAGE_MAP()


// CCrazySpotHelperDlg message handlers

BOOL CCrazySpotHelperDlg::OnInitDialog()
{
	CDialogEx::OnInitDialog();

	// Set the icon for this dialog.  The framework does this automatically
	//  when the application's main window is not a dialog
	SetIcon(m_hIcon, TRUE);			// Set big icon
	SetIcon(m_hIcon, FALSE);		// Set small icon

	// TODO: Add extra initialization here
	m_pDlg = new CTranDlg();   //get A CTranDlg Object.
	m_pDlg->Create(IDD_DIALOG , this);

	SetWindowLong(m_pDlg->GetSafeHwnd(), GWL_EXSTYLE,
		GetWindowLong(m_pDlg->GetSafeHwnd(),GWL_EXSTYLE)^0X80000 | WS_EX_TRANSPARENT
		);
	HINSTANCE hInst = LoadLibrary(_T("User32.DLL"));

	if (hInst)
	{
		typedef BOOL (WINAPI * MYFUNC)(HWND, COLORREF, BYTE, DWORD);
		MYFUNC fun =NULL;

		fun = (MYFUNC) GetProcAddress(hInst,"SetLayeredWindowAttributes");
		if (fun)
			fun(m_pDlg->GetSafeHwnd(),0,128,2);
		FreeLibrary(hInst);
	}

	return TRUE;  // return TRUE  unless you set the focus to a control
}

// If you add a minimize button to your dialog, you will need the code below
//  to draw the icon.  For MFC applications using the document/view model,
//  this is automatically done for you by the framework.

void CCrazySpotHelperDlg::OnPaint()
{
	if (IsIconic())
	{
		CPaintDC dc(this); // device context for painting

		SendMessage(WM_ICONERASEBKGND, reinterpret_cast<WPARAM>(dc.GetSafeHdc()), 0);

		// Center icon in client rectangle
		int cxIcon = GetSystemMetrics(SM_CXICON);
		int cyIcon = GetSystemMetrics(SM_CYICON);
		CRect rect;
		GetClientRect(&rect);
		int x = (rect.Width() - cxIcon + 1) / 2;
		int y = (rect.Height() - cyIcon + 1) / 2;

		// Draw the icon
		dc.DrawIcon(x, y, m_hIcon);
	}
	else
	{
		CDialogEx::OnPaint();
	}
}

// The system calls this function to obtain the cursor to display while the user drags
//  the minimized window.
HCURSOR CCrazySpotHelperDlg::OnQueryDragIcon()
{
	return static_cast<HCURSOR>(m_hIcon);
}



bool CCrazySpotHelperDlg::GetGameHandle(void)
{
	m_pGame = FindWindow(NULL, _T("大家来找茬"));
	if (m_pGame==NULL)
		return false;
	else
		return true;
}

bool CCrazySpotHelperDlg::GetPicByCap(CBitmap &bitmap_left, CBitmap &bitmap_right, CBitmap &bitmap_buffer)
{

	CDC *pSrcDC = m_pGame->GetDC(); 
    CDC memDC;    
    memDC.CreateCompatibleDC(pSrcDC);

    bitmap_left.CreateCompatibleBitmap(pSrcDC,nPicWidth,nPicHeight); 
	bitmap_buffer.CreateCompatibleBitmap(pSrcDC,nPicWidth,nPicHeight);
    bitmap_right.CreateCompatibleBitmap(pSrcDC,nPicWidth,nPicHeight);
 
	//Get the left Picture's Point
    CBitmap* pOldBitmap = memDC.SelectObject(&bitmap_left);      	
	memDC.BitBlt(0,0,nPicWidth,nPicHeight,pSrcDC,nOffsetLeftPicX,nOffsetLeftPicY,SRCCOPY);
	//Get the left Picture's Point
	memDC.SelectObject(&bitmap_buffer);      
    memDC.BitBlt(0,0,nPicWidth,nPicHeight,pSrcDC,nOffsetRightPicX,nOffsetRightPicY,SRCCOPY);
	//Get the right Picture's Point
    memDC.SelectObject(&bitmap_right);      
    memDC.BitBlt(0,0,nPicWidth,nPicHeight,pSrcDC,nOffsetRightPicX,nOffsetRightPicY,SRCCOPY);
	memDC.SelectObject(pOldBitmap);

	if ( &bitmap_left == NULL || &bitmap_right == NULL)
		return false;
	else 
		return true;
}

void CCrazySpotHelperDlg::Compare_Ex(CBitmap &bitmap_left, CBitmap &bitmap_right, CImage* &pBuffer)
{
	if (!pBuffer)
	{
		delete pBuffer;
		pBuffer = NULL;
	}
//	pBuffer = new DWORD[ nPicHeight*nPicWidth ];

	CImage image_left;
    CImage image_right;

    image_left.Attach(bitmap_left);
    image_right.Attach(bitmap_right);


#ifndef NO_DEBUG_1
	std::ofstream os("check.txt");
#endif



	//Compare the Pixel One by One
    for(int i=0; i<nPicWidth;i++){
        for(int j=0; j<nPicHeight; j++){
            
			if(
				image_left.GetPixel(i,j) != image_right.GetPixel(i,j)
				)
				pBuffer->SetPixel(i,j,RGB(255,0,0));
			else
				pBuffer->SetPixel(i,j,RGB(255,255,255));
			
#ifndef NO_DEBUG_1
//			os<<image_left.GetPixel(i,j)<<std::endl
//				<<image_right.GetPixel(i,j)<<std::endl<<std::endl;
			os<<pBuffer[ i*nPicHeight+j ]<<std::endl;
//			CClientDC dc(this);
//			image_right.BitBlt(dc,0,0);
#endif
        }
    }

#ifndef NO_DEBUG_1
	os.close();
#endif


	image_left.Destroy();
	image_right.Destroy();
}

void CCrazySpotHelperDlg::OnBnClickedCancel()
{
	// TODO: Add your control notification handler code here
	CDialogEx::OnCancel();
}

void CCrazySpotHelperDlg::OnBnClickedOk()
{
	// TODO: Add your control notification handler code here
	if (!GetGameHandle())
	{
		MessageBox(_T("未发现客户端"));
		return;
	}

	CBitmap bitmap_buffer;
	CBitmap bitmap_left;
    CBitmap bitmap_right;
	CImage* pBuffer = new CImage();

#ifndef NO_DEBUG_3
	STARTTIME()
#endif
	if ( !GetPicByCap(bitmap_left, bitmap_right, bitmap_buffer) )
	{
		MessageBox(_T("未发现客户端"));
		return;
	}
#ifndef NO_DEBUG_3
	ENDTIME()
#endif

	pBuffer->Attach(bitmap_buffer);

#ifndef NO_DEBUG_3
	STARTTIME()
#endif
	Compare_Ex(bitmap_left, bitmap_right, pBuffer);
#ifndef NO_DEBUG_3
	ENDTIME()
#endif


#ifndef NO_DEBUG_2
	std::ofstream os("check2.txt");
	for(int i =0;i<10000;i++)
		os<<pBuffer[i]<<std::endl;
	os.clear();
	os.close();
#endif


#ifndef NO_DEBUG_3
	STARTTIME()
#endif
	ShowFault(pBuffer);
#ifndef NO_DEBUG_3
	ENDTIME()
		std::ofstream os("check3.txt",std::ios::app);
	os<<"GetPicCapTime: "<<(double)(finish.at(0)-start.at(0))/CLOCKS_PER_SEC<<"秒！"<<std::endl
		<<"CompareTime: "<<(double)(finish.at(1)-start.at(1))/CLOCKS_PER_SEC<<"秒！"<<std::endl
		<<"ShowFaultTime: "<<(double)(finish.at(2)-start.at(2))/CLOCKS_PER_SEC<<"秒！"<<std::endl<<std::endl;
	os.close();
#endif


}

void CCrazySpotHelperDlg::ShowFault(const CImage* pBuffer)
{
	if (!pBuffer)
	{
		MessageBox(_T("获取图像信息失败..."));
		return;
	}

	CRect rect;
	m_pGame->GetClientRect(&rect);
	m_pGame->ClientToScreen(&rect);	
	m_pDlg->SetWindowPos(&wndTopMost, rect.left+nOffsetRightPicX, rect.top+nOffsetLeftPicY,
		nPicWidth,nPicHeight,SWP_SHOWWINDOW);
	
	CClientDC pShow(m_pDlg);
	pBuffer->BitBlt(pShow,0,0);
}

//Image Copy..I Copy it From Mr.Hu, But it's uselesss..-_-||
bool CCrazySpotHelperDlg::ImageCopy(const CImage &srcImage,CImage &destImage)
{
	int i;//循环变量
	if(srcImage.IsNull())
	   return FALSE;
	//源图像参数
	BYTE* srcPtr=(BYTE*)srcImage.GetBits();
	int srcBitsCount=srcImage.GetBPP();
	int srcWidth=srcImage.GetWidth();
	int srcHeight=srcImage.GetHeight();
	int srcPitch=srcImage.GetPitch(); 
   //销毁原有图像
   if( !destImage.IsNull())
   {
		destImage.Destroy();
   }

   //创建新图像
   if(srcBitsCount==32)   //支持alpha通道
   {
	    destImage.Create(srcWidth,srcHeight,srcBitsCount,1);
   }
   else
   {
        destImage.Create(srcWidth,srcHeight,srcBitsCount,0);
   }

   //加载调色板
   if(srcBitsCount<=8&&srcImage.IsIndexed())//需要调色板
   {
		RGBQUAD pal[256];
		int nColors=srcImage.GetMaxColorTableEntries();
		if(nColors>0)
		{     
			srcImage.GetColorTable(0,nColors,pal);
			destImage.SetColorTable(0,nColors,pal);//复制调色板程序
		}   

   } 
   //目标图像参数
   BYTE *destPtr=(BYTE*)destImage.GetBits();
   int destPitch=destImage.GetPitch();

   //复制图像数据
   for(i=0 ; i<srcHeight;i++)
   {
	    memcpy( destPtr+i*destPitch, srcPtr+i*srcPitch, abs(srcPitch) );
	} 
   
    return TRUE;
}
