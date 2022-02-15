import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast, ToastContainer } from 'react-toastify'
import { Grid, Header, Segment, Transition } from 'semantic-ui-react'
import FoodsItem from './components/FoodsItem'
import SiteMenu from './components/SiteMenu'
import { IFoods, ResultFoods } from './models/IFoods'
import { pendingFoodList} from './Services'
import { autControl, control } from './Util'

export default function AdminWaitFoodList() {

  const navigate = useNavigate()
  const [foodsArr, setFoodsArr] = useState<ResultFoods[]>([]);
  var isAdmin = false
    
  // animation
  const [visible, setVisible] = useState(false)
  useEffect(() => {
    const usr = control()
    if (usr !== null) {
      usr.roles!.forEach(item => {
        if ( item.name === "ROLE_admin" ) {
          isAdmin = true
        }
      });
    }
    
    if( autControl() === null || isAdmin === false ) {
      localStorage.removeItem("user")
      localStorage.removeItem("aut")
      navigate("/")
    }
    setTimeout(() => {
      setVisible(true)
    }, 500);

    setTimeout(() => {
      setVisible(false)
    }, 2000);


    // user food list service
    toast.loading("Yükleniyor.")
    pendingFoodList().then( res => {
        const dt:IFoods = res.data;
        setFoodsArr( dt.result! )
        toast.dismiss(); 
    }).catch(err => {
        toast.dismiss();
        toast.error( "Ürün listeleme işlemi sırasında bir hata oluştu!" )
    })

  }, [])

  

  return (
    <>
        <ToastContainer />
        <SiteMenu />
        <Header style={{backgroundColor:'white'}} color='teal' as='h3' block>
          Onay Bekleyen Gıdalar
        </Header>
        <Transition visible={visible} animation='slide down' duration={750}>
        <Segment vertical color='grey'  >
            Kullanıcıların eklediği onay bekleyen gıdalar
        </Segment>
        </Transition>

        <Grid >
            { foodsArr.map((item, index) => 
                <FoodsItem  key={index} item={item} status={true} isAdmin={true} /> 
            )}
        </Grid>
    </>
  )
}
