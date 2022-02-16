import React, { useEffect, useState } from 'react'
import { Menu, Button, Modal, Form, Segment, Label, Grid, Header, Message } from 'semantic-ui-react'
import { cities } from '../Datas';
import { IUser, UserResult } from '../models/IUser';
import { userAndAdminLogin, logout, userRegister } from '../Services';
import { toast } from 'react-toastify';
import { control, encryptData } from '../Util'
import { Link, useLocation, useNavigate } from 'react-router-dom';


export default function SiteMenu() {

  const [activeItem, setActiveItem] = useState("Anasayfa")

  // modal delete state
  const [modalStatus, setModalStatus] = useState(false);
  const [modalLoginStatus, setModalLoginStatus] = useState(false)

  // login and register states
  const [userName, setUserName] = useState("");
  const [userSurname, setUserSurname] = useState("");
  const [userPhone, setUserPhone] = useState("");
  const [userMail, setUserMail] = useState("");
  const [userPass, setUserPass] = useState("");
  const [cityId, setCityId] = useState('0')

  // login user object
  const [user, setUser] = useState<UserResult | null>()

  // logout
  const [isLogOut, setIsLogOut] = useState(false)
  const [isAdmin, setIsAdmin] = useState(false)

  // login status
  const [loginStatus, setLoginStatus] = useState(false)
  useEffect(() => {
    urlActive()
    const usr = control()
    if (usr !== null) {
      setUser(usr)
      usr.roles!.forEach(item => {
        if (item.name === "ROLE_admin") {
          setIsAdmin(true)
        }
      });
    }
  }, [loginStatus])

  // url control and menu active
  const urlActive = () => {
    if (loc.pathname === "/") {
      setActiveItem("Anasayfa")
    }
    if (loc.pathname === "/foodsAdd") {
      setActiveItem("Gıda Ekle")
    }
    if (loc.pathname === "/foodsList") {
      setActiveItem("Eklediklerim")
    }
    if (loc.pathname === "/pendingList") {
      setActiveItem("Bekleyenler")
    }
  }

  // useNavigate
  const navigate = useNavigate()
  const loc = useLocation()

  const handleItemClick = (name: string) => {
    console.log('name', name)
    setActiveItem(name)

    if (name === "Anasayfa") {
      navigate("/")
    }

    if (name === "Gıda Ekle") {
      if (control() === null) {
        setModalLoginStatus(true);
      } else {
        navigate("/foodsAdd")
      }
    }


    if (name === "Eklediklerim") {
      if (control() === null) {
        setModalLoginStatus(true);
      } else {
        navigate("/foodsList")
      }
    }

    if (name === "Bekleyenler") {
      if (control() === null) {
        setModalLoginStatus(true);
      } else {
        navigate("/pendingList")
      }
    }

  }

  const showModal = () => {
    setModalStatus(true);
  }

  const showLoginModalStatus = () => {
    setModalLoginStatus(true);
  }


  // login fnc
  let regemail = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w\w+)+$/;
  const login = (e: React.FormEvent) => {
    e.preventDefault()
    if (userMail ==='') {
      toast.warning('Lütfen email alanını doldurunuz!');
    } else if (regemail.test(userMail) === false) {
      toast.warning('Lütfen geçerli bir email giriniz!')
    } else if (userPass === '') {
      toast.warning('Lütfen şifre alanını doldurunuz!');
    } else {
      toast.loading("Yükleniyor.")
      userAndAdminLogin(userMail, userPass).then(res => {
        const usr: IUser = res.data
        if (usr.status!) {
          const userResult = usr.result!
          // key
          const key = process.env.REACT_APP_SALT
          const cryptString = encryptData(userResult, key!)
          const userAutString = encryptData(res.config.headers, key!)
          localStorage.setItem("user", cryptString)
          localStorage.setItem("aut", userAutString)
          setLoginStatus(usr.status!)
          setModalLoginStatus(false)
        }
        toast.dismiss();
      }).catch(err => {
        toast.dismiss();
        toast.error("Bu yetkilerde bir kullanıcı yok!")
      })
    }
  }


  // register fnc
  let regphone = /^[0]?[5]\d{9}$/;
  const strongRegex = new RegExp("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%\^&\*])(?=.{8,})");
  const register = (e: React.FormEvent) => {
    e.preventDefault()
    if (userName === '') {
      toast.warning('Lütfen isim alanını doldurunuz!');
    } else if (userSurname === '') {
      toast.warning('Lütfen soyadı alanını doldurunuz!');
    } else if (userPhone === '') {
      toast.warning('Lütfen telefon alanını doldurunuz!');
    } else if (regphone.test(userPhone) === false) {
      toast.warning('Lütfen geçerli bir telefon numarası giriniz!');
    } else if (userMail === '') {
      toast.warning('Lütfen email alanını doldurunuz!');
    } else if (regemail.test(userMail) === false) {
      toast.warning('Lütfen geçerli bir email giriniz!')
    }
    else if (userPass === '') {
      toast.warning('Lütfen şifre alanını doldurunuz!');
    } else if (userPass.length <= 8) {
      toast.warning('Şifre 8 karakterden kısa olamaz!');
    }
    else if (!strongRegex.test(userPass)) {
      toast.warning('Şifreniz en az bir büyük bir küçük harf özel işaret ve numara içermelidir!');
    } else {
      toast.loading("Yükleniyor.")
      userRegister(userName, userSurname, parseInt(cityId), userPhone, userMail, userPass)
        .then(res => {

          const usr: IUser = res.data
          toast.dismiss();
          if (usr.status) {
            // kayıt başarılı
            toast.info("Kayıt işlemi başarılı oldu, Lütfen giriş yapınız")
            setModalStatus(false)
            setModalLoginStatus(true)
          } else {
            toast.error(usr.message)
          }

        }).catch(err => {
          toast.dismiss();
          toast.error("Kayıt işlemi sırasında bir hata oluştu!")
        })
    }
  }

  // log out fnc
  const fncLogOut = () => {
    toast.loading("Yükleniyor.")
    logout().then(res => {
      localStorage.removeItem("user")
      setIsLogOut(false)
      setUser(null)
      setLoginStatus(false)
      setIsAdmin(false)
      toast.dismiss();
      window.location.href = "/"
    }).catch(err => {
      toast.dismiss();
      toast.error("Çıkış işlemi sırasında bir hata oluştu!")
    })
  }


  return (
    <>

      <Menu tabular>
        <Menu.Item>
          <img alt="logo" src='/logo.png'/>
        </Menu.Item>
        <Menu.Item
          name='Anasayfa'
          active={activeItem === 'Anasayfa'}
          onClick={(e, data) => handleItemClick(data.name!)}
          color='teal'
        />
        <Menu.Item
          name='Gıda Ekle'
          active={activeItem === 'Gıda Ekle'}
          onClick={(e, data) => handleItemClick(data.name!)}
          color='teal'
        />
        <Menu.Item
          name='Eklediklerim'
          active={activeItem === 'Eklediklerim'}
          onClick={(e, data) => handleItemClick(data.name!)}
          color='teal'
        />

        {isAdmin === true &&
          <Menu.Item
            name='Bekleyenler'
            active={activeItem === 'Bekleyenler'}
            onClick={(e, data) => handleItemClick(data.name!)}
            color='teal'
          />
        }

        <Menu.Menu position='right'>

          {!user &&
            <>
              <Menu.Item
                name='Giriş Yap'
                active={activeItem === 'Giriş Yap'}
                onClick={(e, data) => showLoginModalStatus()}
              />
              <Menu.Item
                name='Kayıt Ol'
                active={activeItem === 'Kayıt Ol'}
                onClick={(e, data) => showModal()}
              />
            </>}

          {user &&
            <>

              <Menu.Item>
                <Label size='medium' as='a' color='teal' image >
                  <img src='./user.png' />
                  {user.name} {user.surname}
                </Label>
              </Menu.Item>

              <Menu.Item
                name='Çıkış Yap'
                active={activeItem === 'Çıkış Yap'}
                onClick={(e, data) => setIsLogOut(true)}
              />
            </>}

        </Menu.Menu>
      </Menu>

      {/* register modal */}
      <Modal
        centered={false}
        style={{ maxWidth: 450 }}
        open={modalStatus}
        onClose={() => setModalStatus(false)}
      >
        <Segment stacked>

          <Modal.Content>
            <Grid textAlign='center' >
              <Grid.Column >
                <Header as='h2' color='teal' textAlign='center'>
                  Kayıt Ol
                </Header>
                <Form size='large' onSubmit={(e) => login(e)}>

                  <Form.Input
                    value={userName}
                    onChange={(e) => setUserName(e.target.value)}
                    fluid
                    icon='user'
                    iconPosition='left'
                    placeholder='Ad' />

                  <Form.Input
                    value={userSurname}
                    onChange={(e, d) => setUserSurname(d.value)}
                    fluid
                    icon='user'
                    iconPosition='left'
                    placeholder='Soyad' />

                  <Form.Select
                    value={cityId}
                    onChange={(e, d) => setCityId("" + d.value)}
                    fluid
                    options={cities}
                    search />


                  <Form.Input
                    value={userPhone}
                    onChange={(e) => setUserPhone(e.target.value)}
                    fluid
                    icon='mobile'
                    iconPosition='left'
                    placeholder='Telefon Numarası' />

                  <Form.Input
                    value={userMail}
                    onChange={(e) => setUserMail(e.target.value)}
                    fluid
                    icon='mail'
                    iconPosition='left'
                    placeholder='E-Mail' />


                  <Form.Input
                    value={userPass}
                    onChange={(e) => setUserPass(e.target.value)}
                    fluid
                    icon='lock'
                    iconPosition='left'
                    placeholder='Parola'
                    type='password' />

                  <Modal.Actions>
                    <Form.Group style={{ justifyContent: 'space-between', }} widths='equal'>
                      <Button onClick={(e) => setModalStatus(false)} style={{ marginLeft: '8px' }} color='red' >
                        Geri Dön
                      </Button>
                      <Button onClick={(e) => register(e)} style={{ marginRight: '8px' }} color='teal' >
                        Kayıt Ol
                      </Button>

                    </Form.Group>

                  </Modal.Actions>
                </Form>
                <Message>
                  Zaten üye misiniz? <Link onClick={(e) => { setModalStatus(false); setModalLoginStatus(true) }} to={''}>Giriş Yap</Link>
                </Message>
              </Grid.Column>
            </Grid>
          </Modal.Content>
        </Segment>

      </Modal>


      {/* login modal */}
      <Modal
        centered={false}
        style={{ maxWidth: 400 }}
        open={modalLoginStatus}
        onClose={() => setModalLoginStatus(false)}
      >
        <Segment stacked>

          <Modal.Content>
            <Grid textAlign='center' >
              <Grid.Column >
                <Header as='h2' color='teal' textAlign='center'>
                  Üye Girişi
                </Header>
                <Form size='large' onSubmit={(e) => login(e)}>

                  <Form.Input
                    value={userMail}
                    onChange={(e, d) => setUserMail(d.value)}
                    fluid
                    icon='mail'
                    iconPosition='left'
                    placeholder='E-mail' />

                  <Form.Input
                    value={userPass}
                    onChange={(e, d) => setUserPass(d.value)}
                    fluid
                    icon='lock'
                    iconPosition='left'
                    placeholder='Parola'
                    type='password' />

                  <Modal.Actions>
                    <Form.Group style={{ justifyContent: 'space-between', }} widths='equal'>
                      <Button onClick={(e) => setModalLoginStatus(false)} style={{ marginLeft: '8px' }} color='red' >
                        Geri Dön
                      </Button>
                      <Button type='submit' style={{ marginRight: '8px' }} color='teal' >
                        Giriş Yap
                      </Button>
                    </Form.Group>
                  </Modal.Actions>
                </Form>
                <Message>
                  Henüz kayıt olmadınız mı? <Link onClick={(e) => { setModalLoginStatus(false); setModalStatus(true) }} to={''}>Kayıt Ol</Link>
                </Message>
              </Grid.Column>
            </Grid>
          </Modal.Content>
        </Segment>
      </Modal>


      {/* logout control
      <Modal
        centered={false}
        style={{ maxWidth: 300 }}
        open={isLogOut}
        onClose={() => setIsLogOut(false)}
      >

        <Modal.Content>
          <Grid textAlign='center' >
            <Grid.Column >

              <Modal.Header as='h2' color='teal' textAlign='center'>
                Çıkış Yap
              </Modal.Header>

              <Modal.Content>
                <p>Çıkmak istediğinizden emin misiniz?</p>
              </Modal.Content>

              <Modal.Actions style={{ justifyContent: 'space-between', }} widths='equal'>
                <Button onClick={() => setIsLogOut(false)} style={{ marginLeft: '8px' }} color='red' >
                  İptal
                </Button>
                <Button onClick={() => fncLogOut()} style={{ marginRight: '8px' }} color='teal' >
                  Çıkış Yap
                </Button>
              </Modal.Actions>

            </Grid.Column>
          </Grid>
        </Modal.Content>


      </Modal> */}


      <Modal
        centered={false}
        size='mini'
        open={isLogOut}
        onClose={() => setIsLogOut(false)}
      >
        <Header as='h3' color='teal' textAlign='center'>
          Çıkış Yap
        </Header>

        <Modal.Content>
          <p>Çıkmak istediğinizden emin misiniz?</p>
        </Modal.Content>
        <Modal.Actions>
          <Button onClick={(e) => setIsLogOut(false)} style={{ marginLeft: '8px' }} color='red' >
            Geri Dön
          </Button>
          <Button onClick={() => fncLogOut()} style={{ marginRight: '8px' }} color='teal' >
            Çıkış Yap
          </Button>
        </Modal.Actions>
      </Modal >

    </>

  )
}