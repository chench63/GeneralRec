
// CrazySpotDlg.cpp : implementation file
//

#include "stdafx.h"
#include "CrazySpot.h"
#include "CrazySpotDlg.h"
#include "afxdialogex.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif


// CCrazySpotDlg dialog




CCrazySpotDlg::CCrazySpotDlg(CWnd* pParent /*=NULL*/)
	: CDialogEx(CCrazySpotDlg::IDD, pParent)
{
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
}

void CCrazySpotDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
}

BEGIN_MESSAGE_MAP(CCrazySpotDlg, CDialogEx)
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	ON_BN_CLICKED(IDOK, &CCrazySpotDlg::OnBnClickedOk)
END_MESSAGE_MAP()


// CCrazySpotDlg message handlers

BOOL CCrazySpotDlg::OnInitDialog()
{
	CDialogEx::OnInitDialog();

	// Set the icon for this dialog.  The framework does this automatically
	//  when the application's main window is not a dialog
	SetIcon(m_hIcon, TRUE);			// Set big icon
	SetIcon(m_hIcon, FALSE);		// Set small icon

	// TODO: Add extra initialization here

	return TRUE;  // return TRUE  unless you set the focus to a control
}

// If you add a minimize button to your dialog, you will need the code below
//  to draw the icon.  For MFC applications using the document/view model,
//  this is automatically done for you by the framework.

void CCrazySpotDlg::OnPaint()
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
HCURSOR CCrazySpotDlg::OnQueryDragIcon()
{
	return static_cast<HCURSOR>(m_hIcon);
}



void CCrazySpotDlg::OnBnClickedOk()
{
	// TODO: Add your control notification handler code here
	 //Picture's Position
    int nPicWidth = 498-1;  
    int nPicHeight = 448;
    int nOffsetLeftPicX = 8;
    int nOffsetLeftPicY = 193;
    int nOffsetRightPicX = 516 + 1;
    int nOffsetRightPicY = 193;

    //Get MainFr 's Point
    CWnd* pGame = FindWindow(NULL, _T("大家来找茬"));    //Tencent's QQGame
 
	if (!pGame){
		MessageBox(_T("未找到客戶端"));
		return ;
	}


	//将游戏数据复制到两个CBitmap对象中
    CDC *pSrcDC=pGame->GetDC(); 
    CDC memDC;    
    memDC.CreateCompatibleDC(pSrcDC);
    CBitmap bitmap_left;
    bitmap_left.CreateCompatibleBitmap(pSrcDC,nPicWidth,nPicHeight); 
    CBitmap bitmap_right;
    bitmap_right.CreateCompatibleBitmap(pSrcDC,nPicWidth,nPicHeight);
 
	//Get the left Picture's Point
    CBitmap* pOldBitmap = memDC.SelectObject(&bitmap_left);      
    memDC.BitBlt(0,0,nPicWidth,nPicHeight,pSrcDC,nOffsetLeftPicX,nOffsetLeftPicY,SRCCOPY);

	//Get the right Picture's Point
    memDC.SelectObject(&bitmap_right);      
    memDC.BitBlt(0,0,nPicWidth,nPicHeight,pSrcDC,nOffsetRightPicX,nOffsetRightPicY,SRCCOPY);
    memDC.SelectObject(pOldBitmap);
 
	//Create two CImage Object to Find the Difference
    CImage image_left;
    CImage image_right;
    image_left.Attach(bitmap_left);
    image_right.Attach(bitmap_right);
    
	//Compare the Pixel One by One
    for(int i=0; i<nPicWidth;i++){
        for(int j=0; j<nPicHeight; j++){
            if(image_left.GetPixel(i,j) != image_right.GetPixel(i,j)){
                image_left.SetPixel(i,j,RGB(255,0,0));
            }
        }
    }
    //Display the Result

    CClientDC dc(this);
	image_left.BitBlt(dc,0,0);


	/*
	CDC* pDC=GetDlgItem(IDC_STATIC)->GetDC();
	HDC hdc = pDC->GetSafeHdc();

	CRect rect;
    int rw=rect.right-rect.left;    // 求出picture control的宽和高
    int rh=rect.bottom-rect.top;
	int iw=image_left.GetWidth();             // 读取图片的宽和高
	int ih=image_left.GetHeight();
    int tx = (int)(rw - iw)/2;                                       // 使图片的显示位置正好在控件的正中
    int ty = (int)(rh - ih)/2;
    SetRect( rect, tx, ty, tx+iw, ty+ih );
	image_left.Draw(hdc,rect);
    ReleaseDC(pDC);
	*/
}